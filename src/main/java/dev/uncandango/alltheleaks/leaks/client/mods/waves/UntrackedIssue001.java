package dev.uncandango.alltheleaks.leaks.client.mods.waves;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import java.util.concurrent.ConcurrentMap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import waves.client.ClientEventHandler;

@Issue(modId = "waves", versionRange = "[1.6.1,)")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearOnEngineUpdate);
   }

   private void clearOnEngineUpdate(UpdateableLevel.RenderEnginesUpdated event) {
      ClientEventHandler.WAVE_PARTICLE_HANDLER.clear();
   }

   static {
      ConcurrentMap var0 = ClientEventHandler.WAVE_PARTICLE_HANDLER;
   }
}
