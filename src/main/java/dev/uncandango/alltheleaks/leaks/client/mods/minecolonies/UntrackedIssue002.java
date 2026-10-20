package dev.uncandango.alltheleaks.leaks.client.mods.minecolonies;

import com.minecolonies.api.MinecoloniesAPIProxy;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.common.NeoForge;

@Issue(
   modId = "minecolonies",
   versionRange = "[1.1.1149-1.21.1-snapshot,)",
   extraModDep = "jei",
   extraModDepVersions = "*",
   mixins = "main.MineColoniesJEIPluginMixin"
)
public class UntrackedIssue002 {
   public static final VarHandle eventHandlersPerType;
   public static final MethodHandle getEventBus;
   public static final Class<?> customRecipesReloadEventClass = ReflectionHelper.getClass("com.minecolonies.api.eventbus.events.CustomRecipesReloadedEvent");

   public UntrackedIssue002() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearCustomRecipeEvent);
   }

   private void clearCustomRecipeEvent(LoggingOut event) {
      if (event.getPlayer() != null) {
         try {
            Object bus = (Object)getEventBus.invoke((MinecoloniesAPIProxy)MinecoloniesAPIProxy.getInstance());
            (Map)eventHandlersPerType.get((Object)bus).remove(customRecipesReloadEventClass);
         } catch (Throwable t) {
            AllTheLeaks.LOGGER.error("Error while trying to get Minecolonies event bus", t);
         }
      }
   }

   static {
      Class<?> defaultEventBusClass = ReflectionHelper.getClass("com.minecolonies.api.eventbus.DefaultEventBus");
      eventHandlersPerType = ReflectionHelper.getFieldFromClass(defaultEventBusClass, "eventHandlersPerType", Map.class, false);
      Class<?> eventBusClass = ReflectionHelper.getClass("com.minecolonies.api.eventbus.EventBus");
      getEventBus = ReflectionHelper.getMethodFromClass(MinecoloniesAPIProxy.class, "getEventBus", MethodType.methodType(eventBusClass), false);
   }
}
