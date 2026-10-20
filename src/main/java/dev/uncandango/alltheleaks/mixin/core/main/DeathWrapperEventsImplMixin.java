package dev.uncandango.alltheleaks.mixin.core.main;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.api.events.OnDeathCallback;
import io.wispforest.accessories.api.events.OnDropCallback;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.cclayer.DeathWrapperEventsImpl;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.compat.CuriosWrappingUtils;

@Mixin(DeathWrapperEventsImpl.class)
public abstract class DeathWrapperEventsImplMixin implements OnDeathCallback, OnDropCallback {
   @Unique
   private static ImmutableList<Tuple<Predicate<ItemStack>, DropRule>> atl$overrides;

   @WrapOperation(
      method = "shouldDrop",
      at = @At(value = "FIELD", target = "Lio/wispforest/cclayer/DeathWrapperEventsImpl;latestDropRules:Ltop/theillusivec4/curios/api/event/DropRulesEvent;")
   )
   private void getOverrides(DeathWrapperEventsImpl instance, DropRulesEvent value, Operation<Void> original) {
      atl$overrides = value.getOverrides();
   }

   @Overwrite
   @Nullable
   public DropRule onDrop(io.wispforest.accessories.api.DropRule dropRule, ItemStack stack, SlotReference reference, DamageSource damageSource) {
      if (atl$overrides != null) {
         UnmodifiableIterator var5 = atl$overrides.iterator();

         while (var5.hasNext()) {
            Tuple<Predicate<ItemStack>, DropRule> override = (Tuple<Predicate<ItemStack>, DropRule>)var5.next();
            if (((Predicate)override.getA()).test(stack)) {
               return CuriosWrappingUtils.convert((DropRule)override.getB());
            }
         }
      }

      return null;
   }
}
