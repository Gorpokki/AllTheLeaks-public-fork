package dev.uncandango.alltheleaks.leaks.common.mods.supplementaries;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.mehvahdjukaar.supplementaries.common.block.tiles.EndermanSkullBlockTile;
import net.mehvahdjukaar.supplementaries.common.items.crafting.WeatheredMapRecipe;
import net.mehvahdjukaar.supplementaries.common.misc.map_data.ColoredMapHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(modId = "supplementaries", versionRange = "[1.21-3.1.8,1.21.1-3.6.0)")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearRemaining);
   }

   private void clearRemaining(ServerStoppedEvent event) {
      WeatheredMapRecipe.onWorldUnload();
      EndermanSkullBlockTile.clearCache();
      ColoredMapHandler.clearIdCache();
   }

   static {
      MethodHandle dummy = ReflectionHelper.getMethodFromClass(WeatheredMapRecipe.class, "onWorldUnload", MethodType.methodType(void.class), true);
      dummy = ReflectionHelper.getMethodFromClass(EndermanSkullBlockTile.class, "clearCache", MethodType.methodType(void.class), true);
      dummy = ReflectionHelper.getMethodFromClass(ColoredMapHandler.class, "clearIdCache", MethodType.methodType(void.class), true);
   }
}
