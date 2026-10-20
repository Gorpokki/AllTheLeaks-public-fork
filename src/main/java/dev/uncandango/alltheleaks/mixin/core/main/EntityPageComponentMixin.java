package dev.uncandango.alltheleaks.mixin.core.main;

import com.llamalad7.mixinextras.sugar.Local;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import net.favouriteless.modopedia.api.Lookup;
import net.favouriteless.modopedia.api.book.Book;
import net.favouriteless.modopedia.client.page_components.EntityPageComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPageComponent.class)
public class EntityPageComponentMixin implements UpdateableLevel<EntityPageComponent> {
   @Shadow
   protected Entity entity;
   @Unique
   private EntityType<?> atl$entityType;
   @Unique
   private CompoundTag atl$entityTag;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void grabObject(CallbackInfo ci) {
      UpdateableLevel.register(this);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void generateLazyEntity(Book book, Lookup lookup, Level level, CallbackInfo ci, @Local(name = "type") EntityType<?> type) {
      this.atl$entityType = type;
      if (lookup.has("tag")) {
         this.atl$entityTag = (CompoundTag)lookup.get("tag").as(CompoundTag.class);
      }
   }

   @Override
   public void atl$onClientLevelUpdated(@Nullable ClientLevel level) {
      if (level != null) {
         this.entity = this.atl$entityType.create(level);
         if (this.entity != null && this.atl$entityTag != null) {
            this.entity.load(this.atl$entityTag);
         }
      } else {
         this.entity = null;
      }
   }
}
