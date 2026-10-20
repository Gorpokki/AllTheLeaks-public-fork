package dev.uncandango.alltheleaks.leaks.common.mods.supplementaries;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.mehvahdjukaar.supplementaries.common.worldgen.WaySignStructure;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(modId = "supplementaries", versionRange = "[1.21-3.1.8,1.21-3.5.0)")
public class UntrackedIssue003 {
   public UntrackedIssue003() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearRemaining);
   }

   private void clearRemaining(ServerStoppedEvent event) {
      WaySignStructure.clearCache();
   }

   static {
      MethodHandle var0 = ReflectionHelper.getMethodFromClass(WaySignStructure.class, "clearCache", MethodType.methodType(void.class), true);
   }
}
