package dev.uncandango.alltheleaks.leaks.client.mods.entity_texture_features;

import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;
import net.neoforged.neoforge.common.NeoForge;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFLruCache;

@Issue(modId = "entity_texture_features", versionRange = "[6.2.5,7.0.0)")
public class UntrackedIssue001 {
   public static final VarHandle ETF$HELDENTITY = ReflectionHelper.getFieldFromClass(LivingEntityRenderer.class, "etf$heldEntity", ETFEntity.class, false);
   public static final VarHandle ETF$ETFPLAYERTEXTURE = ReflectionHelper.getFieldFromClass(
      PlayerRenderer.class, "etf$ETFPlayerTexture", ETFPlayerTexture.class, false
   );
   public static final MethodHandle ETF_INSTANCE = ReflectionHelper.getMethodFromClass(
      ETFManager.class, "getInstance", MethodType.methodType(ETFManager.class), true
   );
   public static final VarHandle PLAYER_TEXTURE_MAP = ReflectionHelper.getFieldFromClass(ETFManager.class, "PLAYER_TEXTURE_MAP", ETFLruCache.class, false);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearCachedEntityFromRenderer);
      gameBus.addListener(this::clearTextureMap);
   }

   private void clearCachedEntityFromRenderer(Post<?, ?> event) {
      ETF$HELDENTITY.set((LivingEntityRenderer)event.getRenderer(), (Void)null);
      if (event.getRenderer() instanceof PlayerRenderer) {
         ETF$ETFPLAYERTEXTURE.set((LivingEntityRenderer)event.getRenderer(), (Void)null);
      }
   }

   private void clearTextureMap(Clone event) {
      try {
         ETFManager runTime = (ETFManager)ETF_INSTANCE.invoke();
         runTime.PLAYER_TEXTURE_MAP.clear();
      } catch (Throwable e) {
         AllTheLeaks.LOGGER.error("Error while trying to clear texture map from ETF", e);
      }
   }
}
