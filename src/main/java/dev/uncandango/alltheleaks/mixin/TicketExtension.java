package dev.uncandango.alltheleaks.mixin;

import net.minecraft.world.level.ChunkPos;

public interface TicketExtension<T> {
   T atl$getKey();

   default boolean atl$isTouching(ChunkPos chunkpos, int range) {
      return !(this.atl$getKey() instanceof ChunkPos key)
         ? false
         : chunkpos.x > key.x - range && chunkpos.x < key.x + range && chunkpos.z > key.z - range && chunkpos.z < key.z + range;
   }
}
