package dev.uncandango.alltheleaks.mixin.core.main;

import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtil;

@Mixin(GeoModel.class)
public class GeoModelMixin {
   @Overwrite(remap = false)
   public <T extends GeoAnimatable> void applyMolangQueries(AnimationState<T> animationState, double animTime) {
      Minecraft mc = Minecraft.getInstance();
      T animatable = (T)animationState.getAnimatable();
      if (mc.level != null) {
         WeakReference<ClientLevel> level = new WeakReference<>(mc.level);
         MathParser.setVariable("query.life_time", () -> animTime / 20.0);
         MathParser.setVariable("query.actor_count", () -> level.get().getEntityCount());
         MathParser.setVariable("query.time_of_day", () -> (float)level.get().getDayTime() / 24000.0F);
         MathParser.setVariable("query.moon_phase", () -> level.get().getMoonPhase());
         if (animatable instanceof Entity entity) {
            WeakReference<Entity> weakEntity = new WeakReference<>(entity);
            MathParser.setVariable("query.distance_from_camera", () -> mc.gameRenderer.getMainCamera().getPosition().distanceTo(weakEntity.get().position()));
            MathParser.setVariable("query.is_on_ground", () -> RenderUtil.booleanToFloat(weakEntity.get().onGround()));
            MathParser.setVariable("query.is_in_water", () -> RenderUtil.booleanToFloat(weakEntity.get().isInWater()));
            MathParser.setVariable("query.is_in_water_or_rain", () -> RenderUtil.booleanToFloat(weakEntity.get().isInWaterRainOrBubble()));
            if (entity instanceof LivingEntity livingEntity) {
               WeakReference<LivingEntity> weakLivingEntity = new WeakReference<>(livingEntity);
               MathParser.setVariable("query.health", () -> weakLivingEntity.get().getHealth());
               MathParser.setVariable("query.max_health", () -> weakLivingEntity.get().getMaxHealth());
               MathParser.setVariable("query.is_on_fire", () -> RenderUtil.booleanToFloat(weakLivingEntity.get().isOnFire()));
               MathParser.setVariable("query.ground_speed", () -> {
                  Vec3 velocity = weakLivingEntity.get().getDeltaMovement();
                  return Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
               });
               MathParser.setVariable(
                  "query.yaw_speed", () -> weakLivingEntity.get().getViewYRot((float)animTime - weakLivingEntity.get().getViewYRot((float)animTime - 0.1F))
               );
            }
         }
      }
   }
}
