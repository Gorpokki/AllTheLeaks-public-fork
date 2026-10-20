package dev.uncandango.alltheleaks.leaks.client.mods.mekanism;

import dev.uncandango.alltheleaks.annotation.Issue;
import mekanism.common.Mekanism;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "mekanism", issueId = "#8221", versionRange = "(,10.7.5]")
public class Issue8221 {
   public Issue8221() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearPlayerState);
   }

   private void clearPlayerState(Unload event) {
      if (event.getLevel().isClientSide()) {
         Mekanism.playerState.init(null);
      }
   }
}
