package dev.uncandango.alltheleaks.mixin.core.debug;

import dev.uncandango.alltheleaks.AllTheLeaks;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
   @Unique
   private boolean atl$tickingBlockEntities;
   @Unique
   private boolean atl$previousTickingBlockEntities;

   @Inject(method = "tick", at = @At("HEAD"))
   private void clearValue(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      this.atl$tickingBlockEntities = false;
   }

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickBlockEntities()V"))
   private void updateTicking(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      this.atl$tickingBlockEntities = true;
   }

   @Inject(method = "tick", at = @At("TAIL"))
   private void logDiff(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      if (this.atl$tickingBlockEntities != this.atl$previousTickingBlockEntities) {
         AllTheLeaks.LOGGER.info("Ticking block entities at {} is now {}", ((ServerLevel)this).dimension().location(), this.atl$tickingBlockEntities);
      }

      this.atl$previousTickingBlockEntities = this.atl$tickingBlockEntities;
   }
}
