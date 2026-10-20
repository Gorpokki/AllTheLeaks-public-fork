package dev.uncandango.alltheleaks.mixin.core.main;

import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = FakePlayer.class, remap = false)
public class FakePlayerMixin {
}
