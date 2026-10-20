package dev.uncandango.alltheleaks.leaks.common.mods.elementalcraft;

import com.google.common.collect.Multimap;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import sirttas.elementalcraft.block.shrine.enderlock.EnderLockHandler;

@Issue(modId = "elementalcraft", versionRange = "[7.1.3,)")
public class UntrackedIssue001 {
   public static final VarHandle ENDER_LOCK_SHRINES = ReflectionHelper.getFieldFromClass(EnderLockHandler.class, "ENDER_LOCK_SHRINES", Multimap.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearOnLevelUnload);
   }

   private void clearOnLevelUnload(Unload event) {
      if (event.getLevel() instanceof Level level) {
         (Multimap)ENDER_LOCK_SHRINES.get().removeAll(level);
      }
   }
}
