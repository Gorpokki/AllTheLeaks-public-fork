package dev.uncandango.alltheleaks.leaks.client.mods.jei;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "jei", versionRange = "[19.32.0.358,)")
public class UntrackedIssue006 {
   public static final VarHandle GRINDSTONE_MENU;
   public static final VarHandle ANVIL_MENU;

   public UntrackedIssue006() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearMenuOnPlayerClone);
      gameBus.addListener(this::clearMenuOnPlayerLogout);
   }

   private void clearMenuOnPlayerClone(Clone event) {
      GRINDSTONE_MENU.set(null);
      ANVIL_MENU.set(null);
   }

   private void clearMenuOnPlayerLogout(LoggingOut event) {
      GRINDSTONE_MENU.set(null);
      ANVIL_MENU.set(null);
   }

   static {
      Class<?> clazz = ReflectionHelper.getClass("mezz.jei.library.plugins.vanilla.grindstone.GrindstoneHelper");
      GRINDSTONE_MENU = ReflectionHelper.getFieldFromClass(clazz, "GRINDSTONE_MENU", GrindstoneMenu.class, true);
      Class<?> clazz2 = ReflectionHelper.getClass("mezz.jei.library.plugins.vanilla.anvil.AnvilHelper");
      ANVIL_MENU = ReflectionHelper.getFieldFromClass(clazz2, "ANVIL_MENU", AnvilMenu.class, true);
   }
}
