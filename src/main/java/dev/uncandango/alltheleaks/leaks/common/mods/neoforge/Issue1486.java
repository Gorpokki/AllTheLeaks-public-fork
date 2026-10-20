package dev.uncandango.alltheleaks.leaks.common.mods.neoforge;

import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.ListenerList;
import net.neoforged.bus.LockHelper;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Issue(issueId = "#1486", modId = "neoforge", versionRange = "[21.,)")
public class Issue1486 {
   public static final VarHandle LISTENER_LIST = ReflectionHelper.getFieldFromClass(EventBus.class, "listenerLists", LockHelper.class, false);
   public static final VarHandle REBUILD = ReflectionHelper.getFieldFromClass(ListenerList.class, "rebuild", boolean.class, false);
   public static final MethodHandle GET_READ_MAP = ReflectionHelper.getMethodFromClass(LockHelper.class, "getReadMap", MethodType.methodType(Map.class), false);

   public Issue1486() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      if (gameBus instanceof EventBus) {
         gameBus.addListener(EventPriority.LOWEST, this::rebuildListenersCache);
      }
   }

   private void rebuildListenersCache(ServerStoppedEvent event) {
      try {
         Object locker = (Object)LISTENER_LIST.get((IEventBus)NeoForge.EVENT_BUS);
         Map<Class<?>, ListenerList> map = (Map)GET_READ_MAP.invoke((Object)locker);

         for (ListenerList listener : map.values()) {
            boolean rebuild = (boolean)REBUILD.get((ListenerList)listener);
            if (rebuild) {
               listener.getListeners();
            }
         }
      } catch (Throwable e) {
         AllTheLeaks.LOGGER.error("Error while trying to rebuild listeners cache", e);
      }
   }
}
