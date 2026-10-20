package dev.uncandango.alltheleaks.leaks.client.mods.craftingtweaks;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "craftingtweaks", versionRange = "[21.1.6,)")
public class UntrackedIssue001 {
   public static final VarHandle UNPLEASANT_BUTTON = ReflectionHelper.getFieldFromClass(
      CraftingTweaksClient.class, "unpleasantButton", AbstractWidget.class, true
   );

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearWidgetOnLogout);
      gameBus.addListener(this::clearWidgetOnClone);
   }

   private void clearWidgetOnClone(Clone event) {
      UNPLEASANT_BUTTON.set(null);
   }

   private void clearWidgetOnLogout(LoggingOut event) {
      UNPLEASANT_BUTTON.set(null);
   }
}
