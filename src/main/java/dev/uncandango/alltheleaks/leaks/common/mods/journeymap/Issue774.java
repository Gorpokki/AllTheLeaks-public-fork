package dev.uncandango.alltheleaks.leaks.common.mods.journeymap;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.JourneymapAccessor;
import journeymap.common.Journeymap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(issueId = "#774", modId = "journeymap", versionRange = "(,1.21-6.0.0-beta.20]", mixins = "main.accessor.JourneymapAccessor")
public class Issue774 {
   public Issue774() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearServerFromInstance);
   }

   private void clearServerFromInstance(ServerStoppedEvent event) {
      if (Journeymap.getInstance() instanceof JourneymapAccessor accessor) {
         accessor.atl$setServer(null);
      }
   }
}
