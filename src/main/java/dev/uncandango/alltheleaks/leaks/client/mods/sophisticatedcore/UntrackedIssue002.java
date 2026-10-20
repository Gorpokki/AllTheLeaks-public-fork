package dev.uncandango.alltheleaks.leaks.client.mods.sophisticatedcore;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import net.p3pp3rf1y.sophisticatedcore.compat.craftingtweaks.CraftingUpgradeTweakUIPart;

@Issue(modId = "sophisticatedcore", versionRange = "[0.6.23,)", extraModDep = "craftingtweaks", extraModDepVersions = "[21.1.1,)")
public class UntrackedIssue002 {
   public UntrackedIssue002() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearInventoryMenu);
      gameBus.addListener(this::clearInventoryMenuOnClearLevel);
   }

   private void clearInventoryMenuOnClearLevel(UpdateableLevel.RenderEnginesUpdated event) {
      if (event.getLevel() == null) {
         CraftingUpgradeTweakUIPart.register();
      }
   }

   private void clearInventoryMenu(Clone event) {
      CraftingUpgradeTweakUIPart.register();
   }

   static {
      MethodHandle var0 = ReflectionHelper.getMethodFromClass(CraftingUpgradeTweakUIPart.class, "register", MethodType.methodType(void.class), true);
   }
}
