package dev.uncandango.alltheleaks.leaks.common.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.redstone.WirelessRedstoneHandler;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "immersiveengineering", versionRange = "[12.2.0-187,)")
public class UntrackedIssue001 {
   public static final VarHandle LEVEL_CACHE = ReflectionHelper.getFieldFromClass(WirelessRedstoneHandler.class, "LEVEL_CACHE", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::onLevelUnload);
   }

   private void onLevelUnload(Unload event) {
      if (event.getLevel() instanceof ServerLevel level) {
         (Map)LEVEL_CACHE.get().remove(level);
      }
   }
}
