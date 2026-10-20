package dev.uncandango.alltheleaks.leaks.client.mods.entity_texture_features;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFLruCache;

@Issue(modId = "entity_texture_features", versionRange = "[7.0.0,)")
public class UntrackedIssue002 {
   public UntrackedIssue002() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::updateEntityFromManager);
   }

   private void updateEntityFromManager(Clone event) {
      ETFPlayerTexture etfTexture = (ETFPlayerTexture)ETFManager.getInstance().PLAYER_TEXTURE_MAP.get(event.getOldPlayer().getUUID());
      if (etfTexture != null) {
         if (event.getNewPlayer() instanceof ETFPlayerEntity playerEntity) {
            etfTexture.player = playerEntity;
         }
      }
   }

   static {
      MethodHandle dummy = ReflectionHelper.getMethodFromClass(ETFManager.class, "getInstance", MethodType.methodType(ETFManager.class), true);
      VarHandle dummy2 = ReflectionHelper.getFieldFromClass(ETFManager.class, "PLAYER_TEXTURE_MAP", ETFLruCache.class, false);
      Class<?> dummy3 = ReflectionHelper.getClass("traben.entity_texture_features.features.player.ETFPlayerTexture");
      Class var3 = ReflectionHelper.getClass("traben.entity_texture_features.features.player.ETFPlayerEntity");
   }
}
