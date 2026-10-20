package dev.uncandango.alltheleaks.leaks.client.mods.ftblibrary;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.BaseScreenAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "ftblibrary", versionRange = "[2101.1.1,)", mixins = "main.accessor.BaseScreenAccessor")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearPreviousFromClone);
      gameBus.addListener(this::clearPreviousFromUnload);
   }

   private void clearPreviousFromClone(Clone event) {
      if (GuiHelper.BLANK_GUI instanceof BaseScreenAccessor accessor) {
         accessor.atl$setPrevScreen(null);
      }
   }

   private void clearPreviousFromUnload(Unload event) {
      if (event.getLevel().isClientSide() && GuiHelper.BLANK_GUI instanceof BaseScreenAccessor accessor) {
         accessor.atl$setPrevScreen(null);
      }
   }
}
