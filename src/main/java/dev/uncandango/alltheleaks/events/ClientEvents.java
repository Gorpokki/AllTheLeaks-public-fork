package dev.uncandango.alltheleaks.events;

import dev.uncandango.alltheleaks.commands.ATLCommands;
import dev.uncandango.alltheleaks.config.ATLProperties;
import dev.uncandango.alltheleaks.exceptions.ATLUnsupportedOperation;
import dev.uncandango.alltheleaks.feature.common.mods.minecraft.MemoryMonitor;
import dev.uncandango.alltheleaks.mixin.Trackable;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.report.ReportManager;
import dev.uncandango.alltheleaks.utils.MemoryStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.DebugText;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "alltheleaks")
public class ClientEvents {
   private static final boolean IS_BETTERF3_LOADED = LoadingModList.get().getModFileById("betterf3") != null;

   @SubscribeEvent
   public static void addDebugOSMemoryUsed(DebugText event) {
      if (MemoryStats.ENABLED && !IS_BETTERF3_LOADED) {
         if (4 > event.getRight().size()) {
            event.getRight().addLast(MemoryStats.getMemoryWorkingSetSize());
         } else {
            event.getRight().add(4, MemoryStats.getMemoryWorkingSetSize());
         }
      }
   }

   @SubscribeEvent
   public static void clientCommands(RegisterClientCommandsEvent event) {
      ATLCommands.registerClientCommands(event.getDispatcher(), event.getBuildContext());
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      ReportManager.tick();
   }

   @SubscribeEvent
   public static void onWorldSelection(Opening event) {
      Screen screen = event.getNewScreen();
      if (screen instanceof DatapackLoadFailureScreen || screen instanceof DisconnectedScreen || screen instanceof SelectWorldScreen) {
         int errors = ATLUnsupportedOperation.getErrorCount();
         if (errors > 0) {
            ToastComponent toastcomponent = Minecraft.getInstance().getToasts();
            SystemToast.addOrUpdate(
               toastcomponent,
               SystemToastId.WORLD_ACCESS_FAILURE,
               Component.literal("[AllTheLeaks] Errors"),
               Component.translatable("%s errors was found due to Ingredient Dedupe feature", new Object[]{errors})
            );
         }
      }
   }

   @SubscribeEvent
   public static void onEntityLeaveLevelEvent(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof RemotePlayer player) {
         Trackable.startTracking(player);
      }
   }

   @SubscribeEvent
   public static void onPlayerLogin(LoggingIn event) {
      if (event.getConnection().isMemoryConnection()) {
         MemoryMonitor.EventStatistics.WORLD_JOIN_SINGLEPLAYER.increment();
      } else {
         MemoryMonitor.EventStatistics.WORLD_JOIN_MULTIPLAYER.increment();
      }
   }

   @SubscribeEvent
   public static void onClientPlayerClone(Clone event) {
      Trackable.startTracking(event.getOldPlayer());
      MemoryMonitor.EventStatistics.CLIENT_PLAYER_CLONE.increment();
   }

   @SubscribeEvent
   public static void onClientPlayerLogout(LoggingOut event) {
      Trackable.startTracking(event.getPlayer());
      MemoryMonitor.EventStatistics.CLIENT_PLAYER_LOGOUT.increment();
   }

   @SubscribeEvent
   public static void onRenderLevelUpdate(UpdateableLevel.RenderEnginesUpdated event) {
      MemoryMonitor.EventStatistics.CLIENT_LEVEL_UPDATE.increment();
      Trackable.clearNullReferences();
      if (event.getLevel() != null && !ReportManager.isRegistered("min_memory_tracker")) {
         ReportManager.registerTask("min_memory_tracker", 1, MemoryMonitor.Statistics::evaluateMemory);
      }
   }

   @SubscribeEvent
   public static void onDebugRender(DebugText event) {
      if (ATLProperties.get().showSummaryOnDebugScreen) {
         if (Minecraft.getInstance().getDebugOverlay().showDebugScreen() && Minecraft.getInstance().player != null) {
            if (IS_BETTERF3_LOADED) {
               return;
            }

            event.getLeft().addAll(MemoryMonitor.getFullSummary(MemoryMonitor.DEBUG_MOD_PREFIX, true));
         }
      }
   }

   @SubscribeEvent
   public static void registerClientCommands(RegisterClientCommandsEvent event) {
      ATLCommands.registerClientCommands(event.getDispatcher(), event.getBuildContext());
   }
}
