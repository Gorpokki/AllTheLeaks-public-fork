package dev.uncandango.alltheleaks.leaks.common.mods.connectivity;

import com.connectivity.networkstats.NetworkStatGatherer;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(modId = "connectivity", versionRange = "[5.8,)")
public class UntrackedIssue001 {
   public static final VarHandle MINUTEDATA = ReflectionHelper.getFieldFromClass(NetworkStatGatherer.class, "minuteData", List.class, true);
   public static final VarHandle RECORDINGDURATION = ReflectionHelper.getFieldFromClass(NetworkStatGatherer.class, "recordingDuration", int.class, true);
   public static final VarHandle CONNECTIONPACKETDATA = ReflectionHelper.getFieldFromClass(NetworkStatGatherer.class, "connectionPacketData", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearPacketData);
   }

   private void clearPacketData(ServerStoppedEvent event) {
      MINUTEDATA.set((ArrayList)(new ArrayList<>(Arrays.asList())));
      (Map)CONNECTIONPACKETDATA.get().clear();
   }
}
