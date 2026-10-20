package dev.uncandango.alltheleaks.leaks.common.mods.enderstorage;

import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.network.TankSynchroniser.PlayerItemTankCache;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;

@Issue(modId = "enderstorage", versionRange = "[2.13.0.191,)")
public class UntrackedIssue001 {
   public static final VarHandle PLAYER_ITEM_TANK_STATES = ReflectionHelper.getFieldFromClass(TankSynchroniser.class, "playerItemTankStates", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::updateTankCache);
   }

   private void updateTankCache(Clone event) {
      (Map)PLAYER_ITEM_TANK_STATES.get().put(event.getEntity().getUUID(), new PlayerItemTankCache((ServerPlayer)event.getEntity()));
   }
}
