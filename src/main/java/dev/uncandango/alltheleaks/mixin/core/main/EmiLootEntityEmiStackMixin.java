package dev.uncandango.alltheleaks.mixin.core.main;

import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Slime;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEmiStack.class)
public class EmiLootEntityEmiStackMixin implements UpdateableLevel<EntityEmiStack> {
   @Mutable
   @Shadow
   @Final
   @Nullable
   private Entity entity;

   @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;D)V", at = @At("RETURN"))
   private void grabObject(Entity entity, double scale, CallbackInfo ci) {
      UpdateableLevel.register(this);
   }

   @Override
   public void atl$onClientLevelUpdated(ClientLevel level) {
      if (level == null) {
         this.entity = null;
      } else if (this.entity != null) {
         Entity newEntity = this.entity.getType().create(level);
         if (this.entity instanceof Sheep sheep) {
            ((Sheep)newEntity).setColor(sheep.getColor());
         }

         if (this.entity instanceof Slime slime) {
            ((Slime)newEntity).setSize(slime.getSize(), false);
         }

         this.entity = newEntity;
      }
   }
}
