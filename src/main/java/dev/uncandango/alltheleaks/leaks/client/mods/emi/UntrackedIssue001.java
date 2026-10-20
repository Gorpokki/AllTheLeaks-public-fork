package dev.uncandango.alltheleaks.leaks.client.mods.emi;

import dev.emi.emi.runtime.EmiHistory;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "emi", versionRange = "[1.1.7,)")
public class UntrackedIssue001 {
   public static final MethodHandle CLEAR_HISTORY = ReflectionHelper.getMethodFromClass(EmiHistory.class, "clear", MethodType.methodType(void.class), true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearHistory);
   }

   private void clearHistory(Clone event) {
      try {
         CLEAR_HISTORY.invoke();
      } catch (Throwable e) {
         AllTheLeaks.LOGGER.error("Error while trying to clear EMI history", e);
      }
   }
}
