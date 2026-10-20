package dev.uncandango.alltheleaks.feature.common.mods.minecraft;

import com.sun.management.HotSpotDiagnosticMXBean;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.api.windows.PsApi;
import dev.uncandango.alltheleaks.commands.ATLCommands;
import dev.uncandango.alltheleaks.config.ATLProperties;
import dev.uncandango.alltheleaks.exceptions.ATLIllegalState;
import dev.uncandango.alltheleaks.mixin.Trackable;
import dev.uncandango.alltheleaks.report.ReportManager;
import dev.uncandango.alltheleaks.utils.MemoryStats;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Issue(
   modId = "minecraft",
   issueId = "Memory Monitor",
   versionRange = "1.21.1",
   mixins = {"main.PlayerMixin", "main.ChunkAccessMixin", "main.LevelMixin", "main.MinecraftServerMixin"}
)
public class MemoryMonitor {
   private static final boolean EXPLICIT_GC_DISABLED;
   private static final AtomicLong LAST_RUN_GC = new AtomicLong(Util.getMillis());
   private static final List<String> CACHED_SUMMARY = new ArrayList<>();
   public static final String DEBUG_MOD_PREFIX = ChatFormatting.GREEN + "[AllTheLeaks] " + ChatFormatting.RESET;

   public static boolean runExplicitGc() {
      if (!isExplicitGcDisabled()) {
         System.gc();
      } else if (!diagnosticGcRun()) {
         AllTheLeaks.LOGGER.warn("Tried to run explicit GC but it was disabled.");
         return false;
      }

      if (MemoryStats.ENABLED) {
         PsApi.EmptyWorkingSetOfCurrentProcess();
      }

      LAST_RUN_GC.set(Util.getMillis());
      return true;
   }

   public static void dumpHeap() {
      try {
         MBeanServer server = ManagementFactory.getPlatformMBeanServer();
         HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
            server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class
         );
         Path path = FMLPaths.getOrCreateGameRelativePath(Path.of("heap_dump/")).normalize().toAbsolutePath();
         String filePath = path + "/" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + ".hprof";
         mxBean.dumpHeap(filePath, true);
      } catch (Exception e) {
         AllTheLeaks.LOGGER.error("Error while creating heapdump: {}", e.getMessage());
      }
   }

   public static boolean diagnosticGcRun() {
      try {
         MBeanServer server = ManagementFactory.getPlatformMBeanServer();
         ObjectName diagnostic = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
         MemoryMonitor.DiagnosticGcRun beanProxy = JMX.newMXBeanProxy(server, diagnostic, MemoryMonitor.DiagnosticGcRun.class);
         beanProxy.gcRun();
         return true;
      } catch (Exception e) {
         AllTheLeaks.LOGGER.warn("Failed to run gc with diagnostic proxy: {}", e.getMessage());
         return false;
      }
   }

   public static List<String> getEventsSummary() {
      List<String> lines = new ArrayList<>();
      int worldJoinSingleplayerCount = MemoryMonitor.EventStatistics.WORLD_JOIN_SINGLEPLAYER.getCount();
      int worldJoinMultiplayerCount = MemoryMonitor.EventStatistics.WORLD_JOIN_MULTIPLAYER.getCount();
      int worldJoinTotal = worldJoinSingleplayerCount + worldJoinMultiplayerCount;
      if (worldJoinTotal > 0) {
         lines.add("World join: " + worldJoinTotal);
         lines.add("  Singleplayer: " + worldJoinSingleplayerCount);
         lines.add("  Multiplayer: " + worldJoinMultiplayerCount);
      }

      int clientLevelUpdate = MemoryMonitor.EventStatistics.CLIENT_LEVEL_UPDATE.getCount();
      if (clientLevelUpdate > 0) {
         lines.add("Client Level Updates: " + clientLevelUpdate);
      }

      int clientPlayerLogout = MemoryMonitor.EventStatistics.CLIENT_PLAYER_LOGOUT.getCount();
      if (clientPlayerLogout > 0) {
         lines.add("Client Player Logout: " + clientPlayerLogout);
      }

      int clientPlayerClone = MemoryMonitor.EventStatistics.CLIENT_PLAYER_CLONE.getCount();
      if (clientPlayerClone > 0) {
         lines.add("Client Player Clone: " + clientPlayerClone);
      }

      int serverPlayerClone = MemoryMonitor.EventStatistics.SERVER_PLAYER_CLONE.getCount();
      if (serverPlayerClone > 0) {
         lines.add("Server Player Clone: " + serverPlayerClone);
      }

      int noRemovalReasonErrorCount = ATLIllegalState.TYPE.NO_REMOVAL_REASON.getErrorCount();
      if (noRemovalReasonErrorCount > 0) {
         lines.add("  Invalid Cloned Players: " + noRemovalReasonErrorCount);
      }

      int serverPlayerLogout = MemoryMonitor.EventStatistics.SERVER_PLAYER_LOGOUT.getCount();
      if (serverPlayerLogout > 0) {
         lines.add("Server Player Logout: " + serverPlayerLogout);
      }

      int serverStopCount = MemoryMonitor.EventStatistics.SERVER_STOP.getCount();
      if (serverStopCount > 0) {
         lines.add("Server Stopped: " + serverStopCount);
      }

      return lines;
   }

   public static void logFullSummary(Consumer<String> logger) {
      List<String> events = getEventsSummary();
      logger.accept("Listing events...");
      events.forEach(logger);
      logger.accept("Listing memory leaks so far...");
      getFullSummary("", false).forEach(logger);
   }

   public static String getMemoryStatistics() {
      long base = MemoryMonitor.Statistics.getMinMemoryInMb();
      if (base == 0L) {
         return "Waiting to stabilize [" + MemoryMonitor.Statistics.getStableCount() + "/" + MemoryMonitor.Statistics.getStableThreshold() + "]";
      }

      long current = MemoryMonitor.Statistics.getCurrentMinMemoryInMb();
      long diff = current - base;
      return "B: " + base + "MB / C: " + current + "MB / Diff: +" + diff + "MB";
   }

   public static boolean isExplicitGcDisabled() {
      return EXPLICIT_GC_DISABLED;
   }

   public static long lastRunGc() {
      return LAST_RUN_GC.get();
   }

   public static List<String> getFullSummary(String prefix, boolean padding) {
      List<String> lines = new ArrayList<>();
      if (padding) {
         lines.add(" ");
      }

      lines.add(prefix + getMemoryStatistics());
      List<String> formattedSummary = getFormattedSummary(prefix);
      if (formattedSummary.isEmpty()) {
         lines.add(prefix + "No memory leak detected!");
      } else {
         lines.add(prefix + "Memory Leaks detected: (" + ChatFormatting.GREEN + "/atl force_refresh" + ChatFormatting.RESET + " to update)");
         lines.addAll(formattedSummary);
      }

      return lines;
   }

   public static List<String> getFormattedSummary(String prefix) {
      return !CACHED_SUMMARY.isEmpty() && !prefix.isEmpty() ? CACHED_SUMMARY.stream().map(line -> prefix + line).toList() : CACHED_SUMMARY;
   }

   public static void updateLeakSummary() {
      CACHED_SUMMARY.clear();
      Trackable.getSummary().forEach((baseClazz, summaryMap) -> {
         if (!summaryMap.isEmpty()) {
            CACHED_SUMMARY.add("| " + baseClazz.getSimpleName() + ":");
            summaryMap.forEach((innerClazz, count) -> {
               Module module = innerClazz.getModule();
               String clazzName = innerClazz.getSimpleName();
               if (clazzName.isEmpty()) {
                  String[] splitClazz = innerClazz.getName().split("\\.");
                  clazzName = splitClazz[splitClazz.length - 1];
               }

               if (module != null) {
                  CACHED_SUMMARY.add("|- " + clazzName + " (" + module.getName() + "): " + count);
               } else {
                  CACHED_SUMMARY.add("|- " + clazzName + ": " + count);
               }
            });
         }
      });
   }

   public static void tooMuchMemoryUsage() {
      double percentUsage = (double)MemoryMonitor.Statistics.getCurrentMinMemoryInMb() / MemoryMonitor.Statistics.getMaxMemoryInMb();
      if (percentUsage > ATLProperties.get().memoryUsageWarningPercentage / 100.0F) {
         CommandSourceStack source = null;
         if (FMLEnvironment.dist.isClient()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
               source = player.createCommandSourceStack();
            }
         } else {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
               source = server.createCommandSourceStack();
            }
         }

         if (source == null) {
            return;
         }

         source.sendSystemMessage(
            Component.literal("You reached " + ATLProperties.get().memoryUsageWarningPercentage + "% memory usage, showing leaking objects so far...")
               .withStyle(ChatFormatting.RED)
         );
         source.sendSystemMessage(Component.literal(getMemoryStatistics()));
         ATLCommands.checkLeaking(source, false);
         ReportManager.stop("too_much_memory_usage");
      }
   }

   static {
      boolean explicitGcDisabled;
      try {
         MBeanServer server = ManagementFactory.getPlatformMBeanServer();
         HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
            server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class
         );
         explicitGcDisabled = Boolean.parseBoolean(mxBean.getVMOption("DisableExplicitGC").getValue());
      } catch (Exception e) {
         AllTheLeaks.LOGGER.error("Error while instancing MXBean: {}", e.getMessage());
         explicitGcDisabled = true;
      }

      EXPLICIT_GC_DISABLED = explicitGcDisabled;
   }

   public interface DiagnosticGcRun {
      String gcRun();
   }

   public enum EventStatistics {
      CLIENT_LEVEL_UPDATE(new AtomicInteger()),
      WORLD_JOIN_SINGLEPLAYER(new AtomicInteger()),
      WORLD_JOIN_MULTIPLAYER(new AtomicInteger()),
      CLIENT_PLAYER_CLONE(new AtomicInteger()),
      CLIENT_PLAYER_LOGOUT(new AtomicInteger()),
      SERVER_PLAYER_CLONE(new AtomicInteger()),
      SERVER_PLAYER_LOGOUT(new AtomicInteger()),
      SERVER_STOP(new AtomicInteger());

      private final AtomicInteger count;

      EventStatistics(AtomicInteger count) {
         this.count = count;
      }

      public void increment() {
         this.count.incrementAndGet();
      }

      public int getCount() {
         return this.count.get();
      }
   }

   public static class Statistics {
      private static final long maxMemory = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
      private static long minMemory = 0L;
      private static long currentMinMemory = 0L;
      private static long currentUsedMemory = 0L;
      private static int stableCount = 0;
      private static final int stableTicksThreshold = 10;

      public static void evaluateMemory() {
         if (!FMLEnvironment.dist.isClient() || !Minecraft.getInstance().isPaused() && Minecraft.getInstance().level != null) {
            long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            if (currentUsedMemory > used) {
               if (stableCount < 10) {
                  double diff = currentMinMemory > 0L ? 1.0 - (double)used / currentMinMemory : 1.0;
                  if (Math.abs(diff) > 0.02) {
                     stableCount = 0;
                  } else {
                     stableCount++;
                  }
               }

               currentMinMemory = used;
               if (minMemory == 0L && stableCount >= 10 || currentMinMemory < minMemory) {
                  minMemory = currentMinMemory;
               }
            }

            currentUsedMemory = used;
         }
      }

      public static long getMinMemoryInMb() {
         return minMemory / 1024L / 1024L;
      }

      public static long getStableCount() {
         return stableCount;
      }

      public static long getMaxMemoryInMb() {
         return maxMemory;
      }

      public static long getStableThreshold() {
         return 10L;
      }

      public static void reset() {
         minMemory = 0L;
         stableCount = 0;
      }

      public static long getCurrentMinMemoryInMb() {
         return currentMinMemory / 1024L / 1024L;
      }
   }
}
