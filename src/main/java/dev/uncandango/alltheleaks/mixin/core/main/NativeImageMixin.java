package dev.uncandango.alltheleaks.mixin.core.main;

import com.mojang.blaze3d.platform.NativeImage;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.diag.common.mods.minecraft.DebugNativeImage;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin {
   @Shadow
   private long pixels;
   @Shadow
   @Final
   private boolean useStbFree;

   @Shadow
   @Override
   public abstract String toString();

   @Inject(
      method = {"<init>(Lcom/mojang/blaze3d/platform/NativeImage$Format;IIZ)V", "<init>(Lcom/mojang/blaze3d/platform/NativeImage$Format;IIZJ)V"},
      at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/platform/NativeImage;pixels:J", shift = Shift.AFTER, opcode = 181)
   )
   private void atl$injectAtConstructor(CallbackInfo ci) {
      DebugNativeImage.Key key = new DebugNativeImage.Key(this.pixels, this.useStbFree);
      DebugNativeImage.NATIVE_IMAGES_TRACKER
         .compute(
            key,
            (k, v) -> {
               WeakReference<NativeImage> weakReference = new WeakReference<>((NativeImage)this);
               StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
               DebugNativeImage.Value value = new DebugNativeImage.Value(
                  weakReference, stackTraceElements, "[" + Thread.currentThread().getName() + "] " + this.toString()
               );
               if (v == null) {
                  Set<DebugNativeImage.Value> set = Collections.synchronizedSet(new HashSet<>());
                  set.add(value);
                  return set;
               } else {
                  v.add(value);
                  return v;
               }
            }
         );
   }

   @Inject(method = "close", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/platform/NativeImage;pixels:J", opcode = 181))
   private void atl$injectAtClose(CallbackInfo ci) {
      if (this.pixels == 0L) {
         AllTheLeaks.LOGGER.info("Tried to close a NativeImage {} that was already closed!", this);
      } else {
         DebugNativeImage.Key key = new DebugNativeImage.Key(this.pixels, this.useStbFree);
         DebugNativeImage.NATIVE_IMAGES_TRACKER.compute(key, (k, v) -> {
            if (v == null) {
               AllTheLeaks.LOGGER.info("Tried to close a NativeImage {} that key {} was not found!", this, key);
               return null;
            } else {
               v.removeIf(wr -> wr.imageRef().get() == (NativeImage)this);
               return v;
            }
         });
      }
   }
}
