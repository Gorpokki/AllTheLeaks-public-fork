package dev.uncandango.alltheleaks.leaks.common.mods.badpackets;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Set;
import lol.bai.badpackets.impl.registry.ChannelRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(modId = "badpackets", versionRange = "[0.8.1,)", description = "Clears `ChannelRegistry#handlers` map on server stopped")
public class UntrackedIssue001 {
   public static final VarHandle HANDLERS = ReflectionHelper.getFieldFromClass(ChannelRegistry.class, "handlers", Set.class, false);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearPacketsFromHandler);
   }

   private void clearPacketsFromHandler(ServerStoppedEvent event) {
      (Set)HANDLERS.get((ChannelRegistry)ChannelRegistry.PLAY_C2S).clear();
   }

   static {
      ChannelRegistry var0 = ChannelRegistry.PLAY_C2S;
   }
}
