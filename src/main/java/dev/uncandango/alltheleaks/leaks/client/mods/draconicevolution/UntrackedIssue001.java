package dev.uncandango.alltheleaks.leaks.client.mods.draconicevolution;

import com.brandon3055.draconicevolution.items.MobSoul;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "draconicevolution", versionRange = "*")
public class UntrackedIssue001 {
   public static final VarHandle RENDER_ENTITY_MAP = ReflectionHelper.getFieldFromClass(MobSoul.class, "renderEntityMap", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearEntityMap);
   }

   private void clearEntityMap(UpdateableLevel.RenderEnginesUpdated event) {
      (Map)RENDER_ENTITY_MAP.get().clear();
   }
}
