package dev.uncandango.alltheleaks.plugins;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

@JeiPlugin
public class ATLJeiPlugin implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return ResourceLocation.parse("jei:alltheleaks");
   }

   public void onRuntimeUnavailable() {
      NeoForge.EVENT_BUS.post(new ATLJeiPlugin.RuntimeUnavailableEvent());
   }

   public static class RuntimeUnavailableEvent extends Event {
   }
}
