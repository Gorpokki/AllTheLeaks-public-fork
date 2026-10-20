package dev.uncandango.alltheleaks.leaks.client.mods.relics;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import it.hurts.sskirillss.relics.client.handlers.DescriptionHandler;
import java.lang.invoke.VarHandle;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "relics", versionRange = "[0.10.4,)")
public class UntrackedIssue001 {
   public static final VarHandle SLOT = ReflectionHelper.getFieldFromClass(DescriptionHandler.class, "slot", Slot.class, true);
   public static final VarHandle TICKS_COUNT_OLD = ReflectionHelper.getFieldFromClass(DescriptionHandler.class, "ticksCountOld", int.class, true);
   public static final VarHandle TICKS_COUNT = ReflectionHelper.getFieldFromClass(DescriptionHandler.class, "ticksCount", int.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearHandlder);
      gameBus.addListener(this::clearHandlderOnClone);
   }

   private void clear() {
      SLOT.set(null);
      TICKS_COUNT_OLD.set((int)0);
      TICKS_COUNT.set((int)0);
   }

   private void clearHandlder(Unload event) {
      if (event.getLevel() != null && event.getLevel().isClientSide()) {
         this.clear();
      }
   }

   private void clearHandlderOnClone(Clone event) {
      this.clear();
   }
}
