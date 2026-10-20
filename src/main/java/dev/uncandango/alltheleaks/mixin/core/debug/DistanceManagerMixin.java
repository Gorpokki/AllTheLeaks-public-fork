package dev.uncandango.alltheleaks.mixin.core.debug;

import com.llamalad7.mixinextras.sugar.Local;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.diag.common.mods.minecraft.DebugChunkLoading;
import dev.uncandango.alltheleaks.mixin.TicketExtension;
import dev.uncandango.alltheleaks.mixin.core.debug.accessor.ChunkMapAccessor;
import dev.uncandango.alltheleaks.mixin.core.debug.accessor.ChunkMapDistanceManagerAccessor;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DistanceManager.class)
public class DistanceManagerMixin {
   @Unique
   private final LongSet atl$visitedChunk = new LongOpenHashSet();

   @Inject(
      method = "addTicket(JLnet/minecraft/server/level/Ticket;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/Ticket;getTicketLevel()I", ordinal = 0)
   )
   private void logIfInBetween(long chunkPos, Ticket<?> p_ticket, CallbackInfo ci, @Local(ordinal = 1) Ticket<?> updatedTicket) {
      if (this instanceof ChunkMapDistanceManagerAccessor accessor && accessor.atl$getChunkMap() instanceof ChunkMapAccessor cmAccessor) {
         ResourceKey<Level> dimension = cmAccessor.atl$getServerLevel().dimension();
         List<ChunkPos> trackingPos = DebugChunkLoading.getTrackingChunks(dimension);
         if (trackingPos.isEmpty()) {
            AllTheLeaks.LOGGER.info("{} added to {}", updatedTicket, dimension.location());
            this.atl$printStackTrace(updatedTicket, chunkPos);
         } else {
            List<ChunkPos> chunks = this.atl$inBetween(updatedTicket, trackingPos);
            if (chunks.isEmpty()) {
               return;
            }

            AllTheLeaks.LOGGER
               .info("{} was added to {} and may touch chunks: {}", new Object[]{updatedTicket, dimension.location(), StringUtils.join(chunks, ",")});
            this.atl$printStackTrace(updatedTicket, chunkPos);
         }
      }
   }

   @Unique
   private void atl$printStackTrace(Ticket<?> updatedTicket, long chunkPos) {
      AllTheLeaks.LOGGER
         .error(
            "Ticket %s at chunkpos %s with last pos at %s"
               .formatted(
                  updatedTicket,
                  new ChunkPos(chunkPos).toString(),
                  DebugChunkLoading.lastBlockPos == BlockPos.ZERO ? "undefined" : DebugChunkLoading.lastBlockPos.toShortString()
               ),
            new IllegalStateException("StackTrace:")
         );
   }

   @Unique
   private List<ChunkPos> atl$inBetween(Ticket<?> ticket, List<ChunkPos> chunkPositions) {
      int rangeToCheck = 45 - ticket.getTicketLevel();
      ArrayList<ChunkPos> touchedChunks = new ArrayList<>();
      if (ticket instanceof TicketExtension<?> ticketExtension) {
         for (ChunkPos chunk : chunkPositions) {
            if (ticketExtension.atl$isTouching(chunk, rangeToCheck)) {
               touchedChunks.add(chunk);
            }
         }
      }

      return touchedChunks;
   }
}
