package dev.uncandango.alltheleaks.leaks.client.mods.iceberg;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.CustomItemRendererAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "iceberg", versionRange = "[1.2.8,)", mixins = "main.accessor.CustomItemRendererAccessor")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearEntitiesOnLevelUnload);
   }

   private void clearEntitiesOnLevelUnload(Unload event) {
      if (event.getLevel().isClientSide()) {
         CustomItemRendererAccessor.setWolf(null);
         CustomItemRendererAccessor.setHorse(null);
         CustomItemRendererAccessor.setEntity(null);
         CustomItemRendererAccessor.setArmorStand(null);
      }
   }
}
