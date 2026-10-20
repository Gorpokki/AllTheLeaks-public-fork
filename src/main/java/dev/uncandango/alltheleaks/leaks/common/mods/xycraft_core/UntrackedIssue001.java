package dev.uncandango.alltheleaks.leaks.common.mods.xycraft_core;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.WeakHashMap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import tv.soaryn.xycraft.core.utils.FakePlayerUtils;

@Issue(modId = "xycraft_core", versionRange = "[0.7.45,)")
public class UntrackedIssue001 {
   public static final VarHandle FAKE_PLAYERS = ReflectionHelper.getFieldFromClass(FakePlayerUtils.class, "FAKE_PLAYERS", WeakHashMap.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearOnLevelUnloaded);
   }

   private void clearOnLevelUnloaded(Unload event) {
      if (!event.getLevel().isClientSide()) {
         (WeakHashMap)FAKE_PLAYERS.get().remove(event.getLevel());
      }
   }
}
