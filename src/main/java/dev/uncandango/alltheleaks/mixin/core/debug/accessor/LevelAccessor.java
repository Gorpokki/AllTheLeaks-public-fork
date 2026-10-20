package dev.uncandango.alltheleaks.mixin.core.debug.accessor;

import java.util.ArrayList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Level.class)
public interface LevelAccessor {
   @Accessor("freshBlockEntities")
   ArrayList<BlockEntity> atl$getFreshBlockEntities();
}
