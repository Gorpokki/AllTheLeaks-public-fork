package dev.uncandango.alltheleaks.leaks.common.mods.primalmagick;

import com.verdantartifice.primalmagick.common.mana.network.RouteManager;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "primalmagick", versionRange = "[6.0.7,)")
public class UntrackedIssue001 {
   public static final VarHandle ROUTE_TABLES = ReflectionHelper.getFieldFromClass(RouteManager.class, "ROUTE_TABLES", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::onLevelUnload);
   }

   private void onLevelUnload(Unload event) {
      if (!event.getLevel().isClientSide() && event.getLevel() instanceof Level level) {
         (Map)ROUTE_TABLES.get().remove(level.dimension());
      }
   }
}
