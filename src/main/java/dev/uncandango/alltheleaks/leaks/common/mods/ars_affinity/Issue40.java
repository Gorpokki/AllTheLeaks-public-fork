package dev.uncandango.alltheleaks.leaks.common.mods.ars_affinity;

import com.github.ars_affinity.capability.PlayerAffinityData;
import com.github.ars_affinity.capability.PlayerAffinityDataProvider;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

@Issue(issueId = "#40", modId = "ars_affinity", versionRange = "[1.0,1.0.3)")
public class Issue40 {
   public static final VarHandle PLAYER_DATA_CACHE = ReflectionHelper.getFieldFromClass(PlayerAffinityDataProvider.class, "playerDataCache", Map.class, true);

   public Issue40() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::updatePlayerOnClone);
      gameBus.addListener(this::removePlayerOnLogout);
   }

   private void updatePlayerOnClone(Clone event) {
      (Map)PLAYER_DATA_CACHE.get().computeIfPresent(event.getEntity().getUUID(), (uuid, playerAffinityData) -> {
         playerAffinityData.setPlayer(event.getEntity());
         return (PlayerAffinityData)playerAffinityData;
      });
   }

   private void removePlayerOnLogout(PlayerLoggedOutEvent event) {
      (Map)PLAYER_DATA_CACHE.get().remove(event.getEntity().getUUID());
   }
}
