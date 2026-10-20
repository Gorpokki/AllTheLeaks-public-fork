package dev.uncandango.alltheleaks.leaks.client.mods.doggytalents;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import doggytalents.client.screen.widget.DogInventoryButton;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "doggytalents", versionRange = "[1.18.49,)")
public class UntrackedIssue001 {
   public static final VarHandle INVENTORY_BUTTON = ReflectionHelper.getFieldFromClass(
      DogInventoryButton.class, "inventoryButton", DogInventoryButton.class, true
   );

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearInvButton);
      gameBus.addListener(this::clearInvButtonOnLevelUpdate);
   }

   private void clearInvButtonOnLevelUpdate(UpdateableLevel.RenderEnginesUpdated event) {
      INVENTORY_BUTTON.set(null);
   }

   private void clearInvButton(Clone event) {
      INVENTORY_BUTTON.set(null);
   }
}
