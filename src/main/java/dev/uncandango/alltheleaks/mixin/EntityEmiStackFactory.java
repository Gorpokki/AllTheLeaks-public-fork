package dev.uncandango.alltheleaks.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType.EntityFactory;
import stepsword.mahoutsukai.integration.emi.EntityEmiStack;

public interface EntityEmiStackFactory {
   EntityEmiStack atl$withFactory(EntityFactory<Entity> var1);
}
