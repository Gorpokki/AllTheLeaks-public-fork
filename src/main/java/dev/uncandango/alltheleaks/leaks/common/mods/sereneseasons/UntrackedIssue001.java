package dev.uncandango.alltheleaks.leaks.common.mods.sereneseasons;

import dev.uncandango.alltheleaks.annotation.Issue;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import sereneseasons.season.SeasonHandler;

@Issue(modId = "sereneseasons", versionRange = "[10.1.0.1,)")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearLevelFromSeasonHandler);
   }

   private void clearLevelFromSeasonHandler(Unload event) {
      if (!event.getLevel().isClientSide()) {
         SeasonHandler.lastDayTimes.remove(event.getLevel());
         SeasonHandler.updateTicks.remove(event.getLevel());
      }
   }
}
