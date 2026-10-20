package dev.uncandango.alltheleaks.mixin.core.main;

import dev.engine_room.flywheel.lib.util.LevelAttached;
import dev.uncandango.alltheleaks.leaks.client.mods.flywheel.UntrackedIssue001;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelAttached.class)
public class LevelAttachedMixin<T> {
   @Inject(method = "get", at = @At("HEAD"), cancellable = true)
   private void cancelIfOldWorld(LevelAccessor level, CallbackInfoReturnable<T> cir) {
      if (level instanceof ClientLevel cl && UntrackedIssue001.lastUnloadedLevelHash.get() == System.identityHashCode(cl)) {
         cir.setReturnValue(null);
      }
   }
}
