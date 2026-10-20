package dev.uncandango.alltheleaks.events;

import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.commands.ATLCommands;
import dev.uncandango.alltheleaks.config.ATLProperties;
import dev.uncandango.alltheleaks.diag.server.mods.minecraft.DebugThreadsHooks;
import dev.uncandango.alltheleaks.feature.common.mods.minecraft.MemoryMonitor;
import dev.uncandango.alltheleaks.report.ReportManager;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@EventBusSubscriber(modid = "alltheleaks", value = Dist.DEDICATED_SERVER)
public class ServerEvents {
   @SubscribeEvent
   public static void onServerTick(Post event) {
      ReportManager.tick();
   }

   @SubscribeEvent
   public static void registerServerCommands(RegisterCommandsEvent event) {
      ATLCommands.registerServerCommands(event.getDispatcher(), event.getBuildContext());
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      if (!ReportManager.isRegistered("min_memory_tracker")) {
         ReportManager.registerTask("min_memory_tracker", 1, MemoryMonitor.Statistics::evaluateMemory);
      }
   }

   @SubscribeEvent
   public static void printNonDaemonThreads(ServerStoppedEvent event) {
      AtomicBoolean isFirstRun = new AtomicBoolean(true);
      Thread thread = new Thread(
         () -> {
            while (true) {
               try {
                  if (isFirstRun.get()) {
                     Thread.sleep(10000L);
                  } else {
                     Thread.sleep(30000L);
                  }
               } catch (InterruptedException e) {
                  return;
               }

               Set<Thread> threads = Thread.getAllStackTraces().keySet();
               List<Thread> nonDaemonThreads = threads.stream().filter(t -> !t.isDaemon()).filter(t -> !t.getName().equals("DestroyJavaVM")).toList();
               if (!nonDaemonThreads.isEmpty()) {
                  nonDaemonThreads.forEach(DebugThreadsHooks::prettyPrintEvent);
                  if (isFirstRun.get()) {
                     if (!ATLProperties.get().debugThreadsStuck) {
                        AllTheLeaks.LOGGER
                           .warn(
                              "If you did *NOT CRASH*, consider activating \"debugThreadsStuck\" at \"config/alltheleaks.json\" to find where those stuck threads are from"
                           );
                     }

                     if (CommonEvents.reports > 0) {
                        AllTheLeaks.LOGGER.warn("Seems like you crashed and the server got stuck, you might need to terminate the process!");
                     } else {
                        AllTheLeaks.LOGGER.warn("Seems like server is stuck while trying to close. Read messages above about stuck threads!");
                     }

                     isFirstRun.set(false);
                  }
               }
            }
         }
      );
      thread.setName("Stuck Thread Watcher");
      thread.setDaemon(true);
      thread.start();
   }
}
