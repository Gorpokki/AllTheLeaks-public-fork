package dev.uncandango.alltheleaks.mixin.core.main.accessor;

import java.util.Map;
import mods.railcraft.charge.ChargeNetworkImpl;
import mods.railcraft.charge.ChargeProviderImpl;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChargeProviderImpl.class, remap = false)
public interface ChargeProviderImplAccessor {
   @Accessor("networks")
   Map<ServerLevel, ChargeNetworkImpl> getNetworks();
}
