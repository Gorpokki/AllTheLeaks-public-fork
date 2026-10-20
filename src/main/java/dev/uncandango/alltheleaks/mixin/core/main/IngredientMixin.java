package dev.uncandango.alltheleaks.mixin.core.main;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.uncandango.alltheleaks.feature.common.mods.minecraft.IngredientDedupe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Ingredient.class)
public class IngredientMixin {
   @ModifyReturnValue(method = "fromValues", at = @At("RETURN"))
   private static Ingredient grabIngredient(Ingredient origIngredient) {
      return !origIngredient.isEmpty() && !origIngredient.isCustom() ? IngredientDedupe.intern(origIngredient) : origIngredient;
   }

   @Mixin(Ingredient.class)
   public interface IngredientAccessor {
      @Accessor("itemStacks")
      void setItemStacks(ItemStack[] var1);
   }
}
