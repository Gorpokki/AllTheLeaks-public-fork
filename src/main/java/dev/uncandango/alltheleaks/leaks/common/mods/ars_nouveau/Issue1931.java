package dev.uncandango.alltheleaks.leaks.common.mods.ars_nouveau;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry;
import dev.uncandango.alltheleaks.annotation.Issue;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(issueId = "#1931", modId = "ars_nouveau", versionRange = "[5.8.4,5.10.3]")
public class Issue1931 {
   public Issue1931() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearParamOnUnload);
   }

   private void clearParamOnUnload(Unload event) {
      if (!event.getLevel().isClientSide()
         && AlakarkinosConversionRegistry.LOOT_PARAMS != null
         && AlakarkinosConversionRegistry.LOOT_PARAMS.getLevel() == event.getLevel()) {
         AlakarkinosConversionRegistry.LOOT_PARAMS = null;
      }
   }

   static {
      LootParams var0 = AlakarkinosConversionRegistry.LOOT_PARAMS;
   }
}
