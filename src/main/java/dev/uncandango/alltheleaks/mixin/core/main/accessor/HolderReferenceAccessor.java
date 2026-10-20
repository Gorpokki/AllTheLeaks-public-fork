package dev.uncandango.alltheleaks.mixin.core.main.accessor;

import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Reference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Reference.class)
public interface HolderReferenceAccessor<T> {
   @Accessor("owner")
   HolderOwner<T> atl$getOwner();
}
