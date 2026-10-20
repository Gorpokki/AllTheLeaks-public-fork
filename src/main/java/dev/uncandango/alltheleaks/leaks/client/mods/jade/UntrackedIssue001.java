package dev.uncandango.alltheleaks.leaks.client.mods.jade;

import com.google.common.cache.Cache;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import snownee.jade.JadeClient;
import snownee.jade.impl.ObjectDataCenter;

@Issue(modId = "jade", versionRange = "[15.1.8,)")
public class UntrackedIssue001 {
   private static final VarHandle HIDE_MOD_NAME = ReflectionHelper.getFieldFromClass(JadeClient.class, "hideModName", Cache.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearAccessor);
      gameBus.addListener(this::clearAccessorFromClone);
   }

   private void clearAccessor(Unload event) {
      if (event.getLevel().isClientSide()) {
         ObjectDataCenter.set(null);
         Cache cache = (Cache)HIDE_MOD_NAME.get();
         cache.invalidateAll();
      }
   }

   private void clearAccessorFromClone(Clone event) {
      ObjectDataCenter.set(null);
   }
}
