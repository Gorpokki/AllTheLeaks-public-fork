package dev.uncandango.alltheleaks.leaks.client.mods.jei;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.RecipeTransferManagerAccessor;
import mezz.jei.common.Internal;
import mezz.jei.library.runtime.JeiRuntime;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "jei", versionRange = "[19.19.5.232,)", mixins = "main.accessor.RecipeTransferManagerAccessor")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearCachedInventories);
      gameBus.addListener(this::clearCachedInventoriesOnLevelUnload);
   }

   private void clearCachedInventoriesOnLevelUnload(Unload event) {
      if (event.getLevel().isClientSide()) {
         this.clear();
      }
   }

   private void clear() {
      try {
         JeiRuntime runTime = (JeiRuntime)Internal.getJeiRuntime();
         if (runTime.getRecipeTransferManager() instanceof RecipeTransferManagerAccessor accessor) {
            accessor.getUnsupportedContainers().clear();
         }
      } catch (Exception var4) {
      }
   }

   private void clearCachedInventories(Clone event) {
      this.clear();
   }
}
