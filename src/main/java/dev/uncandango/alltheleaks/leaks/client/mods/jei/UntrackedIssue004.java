package dev.uncandango.alltheleaks.leaks.client.mods.jei;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "jei", versionRange = "[19.22.1.316,19.28)")
public class UntrackedIssue004 {
   public static final VarHandle GRINDSTONE_MENU;

   public UntrackedIssue004() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearMenuOnPlayerClone);
      gameBus.addListener(this::clearMenuOnPlayerLogout);
   }

   private void clearMenuOnPlayerClone(Clone event) {
      GRINDSTONE_MENU.set(null);
   }

   private void clearMenuOnPlayerLogout(LoggingOut event) {
      GRINDSTONE_MENU.set(null);
   }

   static {
      Class<?> clazz = ReflectionHelper.getClass("mezz.jei.library.plugins.vanilla.grindstone.GrindstoneRecipeMaker");
      GRINDSTONE_MENU = ReflectionHelper.getFieldFromClass(clazz, "GRINDSTONE_MENU", GrindstoneMenu.class, true);
   }
}
