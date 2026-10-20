package dev.uncandango.alltheleaks.leaks.client.mods.journeymap;

import dev.uncandango.alltheleaks.annotation.Issue;
import java.util.Map;
import journeymap.common.util.PlayerRadarManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "journeymap", versionRange = "[1.21.1-6.0.0-beta.28,)")
public class UntrackedIssue003 {
   public UntrackedIssue003() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearPlayersWithUnloadingLevel);
   }

   private void clearPlayersWithUnloadingLevel(Unload event) {
      PlayerRadarManager.getInstance()
         .getPlayers()
         .entrySet()
         .removeIf(entry -> entry.getValue() != null && ((Player)entry.getValue()).level() == event.getLevel());
   }

   static {
      Map var0 = PlayerRadarManager.getInstance().getPlayers();
   }
}
