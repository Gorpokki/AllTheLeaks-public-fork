package dev.uncandango.alltheleaks.leaks.common.mods.railcraft;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.ChargeProviderImplAccessor;
import mods.railcraft.charge.ChargeProviderImpl;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(solved = true, issueId = "#241", modId = "railcraft", versionRange = "(,1.2.1]", mixins = "main.accessor.ChargeProviderImplAccessor")
public class Issue241 {
   public Issue241() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearLevelFromNetwork);
   }

   private void clearLevelFromNetwork(Unload event) {
      if (event.getLevel() instanceof ServerLevel level && ChargeProviderImpl.DISTRIBUTION instanceof ChargeProviderImplAccessor accessor) {
         accessor.getNetworks().remove(level);
      }
   }
}
