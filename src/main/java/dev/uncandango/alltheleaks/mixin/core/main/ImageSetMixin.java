package dev.uncandango.alltheleaks.mixin.core.main;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.uncandango.alltheleaks.AllTheLeaks;
import java.util.Map;
import journeymap.client.model.ImageHolder;
import journeymap.client.model.ImageSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ImageSet.class)
public class ImageSetMixin {
   @WrapOperation(
      method = "addHolder(Ljourneymap/client/model/ImageHolder;)Ljourneymap/client/model/ImageHolder;",
      at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
   )
   private Object atl$releaseOldImageHolder(Map instance, Object k, Object v, Operation<ImageHolder> original) {
      ImageHolder replacement = (ImageHolder)original.call(new Object[]{instance, k, v});
      if (replacement != null) {
         AllTheLeaks.LOGGER.info("Clearing ImageHolder that was replaced...");
         replacement.clear();
      }

      return replacement;
   }
}
