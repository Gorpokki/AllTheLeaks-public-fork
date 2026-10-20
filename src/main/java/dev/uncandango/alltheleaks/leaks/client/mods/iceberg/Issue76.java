package dev.uncandango.alltheleaks.leaks.client.mods.iceberg;

import com.anthonyhilyard.iceberg.util.EntityCollector;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "iceberg", versionRange = "[1.2.8,)")
public class Issue76 {
   private static final VarHandle WRAPPED_LEVELS_MAP = ReflectionHelper.getFieldFromClass(EntityCollector.class, "wrappedLevelsMap", Map.class, true);
   private static final VarHandle ENTITY_CACHE = ReflectionHelper.getFieldFromClass(EntityCollector.class, "entityCache", Map.class, true);

   public Issue76() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearMaps);
   }

   private void clearMaps(Unload event) {
      if (event.getLevel().isClientSide()) {
         Map map1 = (Map)WRAPPED_LEVELS_MAP.get();
         map1.clear();
         Map map2 = (Map)ENTITY_CACHE.get();
         map2.clear();
      }
   }
}
