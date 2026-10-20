package dev.uncandango.alltheleaks.mixin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;

public interface UpdateableServerPlayer<T> {
   List<WeakReference<UpdateableServerPlayer<?>>> INSTANCES = new ArrayList<>();

   static <O extends UpdateableServerPlayer<?>> void register(O object) {
      synchronized (INSTANCES) {
         INSTANCES.add(new WeakReference<>(object));
      }
   }

   void atl$onServerPlayerUpdated(ServerPlayer var1);

   @EventBusSubscriber(Dist.CLIENT)
   class Manager {
      @SubscribeEvent
      public static void onPlayerClone(Clone event) {
         synchronized (UpdateableServerPlayer.INSTANCES) {
            Iterator<WeakReference<UpdateableServerPlayer<?>>> it = UpdateableServerPlayer.INSTANCES.iterator();

            while (it.hasNext()) {
               UpdateableServerPlayer<?> instance = it.next().get();
               if (instance != null) {
                  instance.atl$onServerPlayerUpdated((ServerPlayer)event.getEntity());
               } else {
                  it.remove();
               }
            }

            ((ArrayList)UpdateableServerPlayer.INSTANCES).trimToSize();
         }
      }
   }
}
