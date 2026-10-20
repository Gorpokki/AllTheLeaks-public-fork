package dev.uncandango.alltheleaks.mixin.core.debug;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.diag.common.mods.minecraft.DebugChunkLoading;
import java.util.function.IntSupplier;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter.Message;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin {
   @WrapOperation(
      method = "lambda$full$3",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ChunkTaskPriorityQueueSorter;message(Ljava/lang/Runnable;JLjava/util/function/IntSupplier;)Lnet/minecraft/server/level/ChunkTaskPriorityQueueSorter$Message;"
      )
   )
   private static Message<?> logPromotingToFull(
      Runnable task,
      long pos,
      IntSupplier level,
      Operation<Message<Runnable>> original,
      @Local(argsOnly = true) ChunkPos chunkpos,
      @Local(argsOnly = true) GenerationChunkHolder holder,
      @Local(argsOnly = true) WorldGenContext worldGenContext
   ) {
      if (DebugChunkLoading.isTracking(worldGenContext.level().dimension(), chunkpos.toLong())) {
         AllTheLeaks.LOGGER.info("Promoting ({}) chunk {} to full", holder.getClass().getSimpleName() + "@" + Integer.toHexString(holder.hashCode()), chunkpos);
      }

      return (Message<?>)original.call(new Object[]{task, pos, level});
   }
}
