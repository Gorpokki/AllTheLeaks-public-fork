package dev.uncandango.alltheleaks.leaks.client.mods.mousetweaks;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import yalter.mousetweaks.IGuiScreenHandler;
import yalter.mousetweaks.Main;

@Issue(modId = "mousetweaks", versionRange = "[2.26.1,)")
public class UntrackedIssue001 {
   private static final VarHandle OPEN_SCREEN = ReflectionHelper.getFieldFromClass(Main.class, "openScreen", Screen.class, true);
   private static final VarHandle OLD_SELECTED_SLOT = ReflectionHelper.getFieldFromClass(Main.class, "oldSelectedSlot", Slot.class, true);
   private static final VarHandle HANDLER = ReflectionHelper.getFieldFromClass(Main.class, "handler", IGuiScreenHandler.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearScreenFromMod);
   }

   private void clearScreenFromMod(Unload event) {
      if (event.getLevel().isClientSide()) {
         OPEN_SCREEN.set(null);
         OLD_SELECTED_SLOT.set(null);
         HANDLER.set(null);
      }
   }
}
