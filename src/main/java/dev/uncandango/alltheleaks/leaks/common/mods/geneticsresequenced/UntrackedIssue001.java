package dev.uncandango.alltheleaks.leaks.common.mods.geneticsresequenced;

import dev.aaronhowser.mods.geneticsresequenced.util.InventoryListener;
import dev.uncandango.alltheleaks.annotation.Issue;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

@Issue(modId = "geneticsresequenced", versionRange = "[1.1.10,1.5.1]")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::updateListenerOnClone);
      gameBus.addListener(this::updateListenerRespawn);
   }

   private void updateListenerOnClone(Clone event) {
      InventoryListener.Companion.stopListening((ServerPlayer)event.getOriginal());
   }

   private void updateListenerRespawn(PlayerRespawnEvent event) {
      InventoryListener.Companion.startListening((ServerPlayer)event.getEntity());
   }
}
