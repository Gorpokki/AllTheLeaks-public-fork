package dev.uncandango.alltheleaks.leaks.client.mods.entity_model_features;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;
import net.neoforged.neoforge.common.NeoForge;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext.IterationContext;

@Issue(modId = "entity_model_features", versionRange = "[2.2.6,3.0.0)")
public class UntrackedIssue001 {
   public static final VarHandle EMF$HELDITERATION = ReflectionHelper.getFieldFromClass(
      LivingEntityRenderer.class, "emf$heldIteration", IterationContext.class, false
   );

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearCachedEntityFromRenderer);
   }

   private void clearCachedEntityFromRenderer(Post<?, ?> event) {
      EMF$HELDITERATION.set((LivingEntityRenderer)event.getRenderer(), (Void)null);
   }
}
