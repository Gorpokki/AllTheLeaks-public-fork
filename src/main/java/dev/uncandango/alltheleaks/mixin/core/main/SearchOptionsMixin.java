package dev.uncandango.alltheleaks.mixin.core.main;

import me.desht.pneumaticcraft.client.gui.pneumatic_armor.options.SearchOptions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SearchOptions.class, remap = false)
public class SearchOptionsMixin {
}
