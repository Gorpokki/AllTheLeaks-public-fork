package dev.uncandango.alltheleaks.leaks.client.mods.flywheel;

import dev.uncandango.alltheleaks.annotation.Issue;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "flywheel", versionRange = "*", extraModDep = "embeddium", extraModDepVersions = "*", mixins = "main.LevelAttachedMixin")
public class UntrackedIssue001 {
   public static final AtomicInteger lastUnloadedLevelHash = new AtomicInteger();

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(EventPriority.HIGHEST, this::getHashOfUnloadedClientLevels);
   }

   private void getHashOfUnloadedClientLevels(Unload event) {
      if (event.getLevel() instanceof ClientLevel cl) {
         lastUnloadedLevelHash.set(System.identityHashCode(cl));
      }
   }
}
