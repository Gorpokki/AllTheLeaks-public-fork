package dev.uncandango.alltheleaks.leaks.common.mods.industrialforegoing;

import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import dev.uncandango.alltheleaks.annotation.Issue;
import java.util.HashMap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(modId = "industrialforegoing", versionRange = "[1.21-3.6.33,)")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearMapOnServerStopped);
   }

   private void clearMapOnServerStopped(ServerStoppedEvent event) {
      MycelialReactorTile.REACTOR_POSITIONS.clear();
   }

   static {
      HashMap var0 = MycelialReactorTile.REACTOR_POSITIONS;
   }
}
