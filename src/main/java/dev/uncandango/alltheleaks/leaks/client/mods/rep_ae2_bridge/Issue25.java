package dev.uncandango.alltheleaks.leaks.client.mods.rep_ae2_bridge;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.unfamily.repae2bridge.block.entity.RepAE2BridgeBlockEntity;

@Issue(issueId = "#25", modId = "rep_ae2_bridge", versionRange = "[1.0.0.0.0,1.4.0.0.1)")
public class Issue25 {
   public static final VarHandle ACTIVE_BRIDGES = ReflectionHelper.getFieldFromClass(RepAE2BridgeBlockEntity.class, "activeBridges", Set.class, true);

   public Issue25() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearClientSideBridges);
   }

   private void clearClientSideBridges(Unload event) {
      if (event.getLevel().isClientSide()) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         LevelAccessor level = event.getLevel();
         if (server == null) {
            this.clearBridges(event.getLevel());
         } else {
            try {
               server.executeIfPossible(() -> this.clearBridges(level));
            } catch (RejectedExecutionException ignore) {
               this.clearBridges(event.getLevel());
            }
         }
      }
   }

   private void clearBridges(LevelAccessor level) {
      (Set)ACTIVE_BRIDGES.get().removeIf(be -> be.getLevel() == level);
   }
}
