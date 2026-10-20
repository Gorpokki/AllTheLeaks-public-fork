package dev.uncandango.alltheleaks.leaks.client.mods.primalmagick;

import com.verdantartifice.primalmagick.common.research.ResearchManager;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

@Issue(modId = "primalmagick", versionRange = "[6.0.7,)")
public class UntrackedIssue001 {
   public static final VarHandle memoizedAffinityIndexEntries = ReflectionHelper.getFieldFromClass(
      ResearchManager.class, "memoizedAffinityIndexEntries", Function.class, true
   );
   public static final VarHandle memoizedCache;

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::onPlayerClone);
      gameBus.addListener(this::onPlayerLogout);
   }

   private void onPlayerClone(Clone event) {
      clear(event.getNewPlayer());
   }

   private static void clear(@Nullable Player player) {
      Function<?, ?> memoizedObj = (Function)memoizedAffinityIndexEntries.get();
      Map<Player, List<?>> cache = (Map)memoizedCache.get((Function)memoizedObj);
      if (player == null) {
         cache.clear();
      } else {
         Iterator<Entry<Player, List<?>>> it = cache.entrySet().iterator();
         List<?> value = null;

         while (it.hasNext()) {
            Entry<Player, List<?>> entry = it.next();
            if (entry.getKey().getUUID().equals(player.getUUID())) {
               value = entry.getValue();
               it.remove();
               break;
            }
         }

         if (value != null) {
            cache.put(player, value);
         }
      }
   }

   private void onPlayerLogout(LoggingOut event) {
      clear(null);
   }

   static {
      try {
         Class<?> memoizedClass = Class.forName("net.minecraft.Util$8");
         memoizedCache = ReflectionHelper.getFieldFromClass(memoizedClass, "cache", Map.class, false);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
