package dev.uncandango.alltheleaks.mixin.core.main;

import com.direwolf20.justdirethings.common.blockentities.PlayerAccessorBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.containers.handlers.PlayerHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.uncandango.alltheleaks.mixin.UpdateableServerPlayer;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerAccessorBE.class, remap = false)
public abstract class PlayerAccessorBEMixin extends BaseMachineBE implements UpdateableServerPlayer<PlayerAccessorBE> {
   @Shadow
   public ServerPlayer serverPlayer;
   @Shadow
   public HashMap<Direction, PlayerHandler> playerHandlers;

   public PlayerAccessorBEMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
      super(pType, pPos, pBlockState);
   }

   @WrapOperation(
      method = "updateServerPlayer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;getPlayer(Ljava/util/UUID;)Lnet/minecraft/server/level/ServerPlayer;")
   )
   private ServerPlayer validatePlayer(PlayerList instance, UUID playerUUID, Operation<ServerPlayer> original) {
      ServerPlayer result = (ServerPlayer)original.call(new Object[]{instance, playerUUID});
      return result != null && result.isRemoved() ? null : result;
   }

   @Override
   public void atl$onServerPlayerUpdated(ServerPlayer player) {
      if (player.getUUID().equals(this.placedByUUID)) {
         this.playerHandlers.clear();
         this.serverPlayer = null;
      }
   }

   public void onLoad() {
      super.onLoad();
      if (this.hasLevel() && !this.getLevel().isClientSide()) {
         UpdateableServerPlayer.register(this);
      }
   }
}
