package dev.uncandango.alltheleaks.leaks.client.mods.mekanism;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "mekanism", versionRange = "[10.7.8,)")
public class UntrackedIssue001 {
   public static final MethodHandle CLEAR_CLIENT_NETWORKS = ReflectionHelper.getMethodFromClass(
      TransmitterNetworkRegistry.class, "clearClientNetworks", MethodType.methodType(void.class), true
   );

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearNetworks);
   }

   private void clearNetworks(UpdateableLevel.RenderEnginesUpdated event) {
      try {
         CLEAR_CLIENT_NETWORKS.invoke();
      } catch (Throwable var3) {
      }
   }
}
