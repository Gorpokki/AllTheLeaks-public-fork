package dev.uncandango.alltheleaks.leaks.common.mods.sfm;

import ca.teamdman.sfm.common.watertanknetwork.WaterNetworkManager;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(modId = "sfm", versionRange = "[4.21.0,4.29.0)")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearOnServerStopped);
   }

   private void clearOnServerStopped(ServerStoppedEvent event) {
      WaterNetworkManager.clear();
   }

   static {
      MethodHandle var0 = ReflectionHelper.getMethodFromClass(WaterNetworkManager.class, "clear", MethodType.methodType(void.class), true);
   }
}
