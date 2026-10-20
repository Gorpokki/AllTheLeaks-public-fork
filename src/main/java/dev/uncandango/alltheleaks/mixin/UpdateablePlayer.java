package dev.uncandango.alltheleaks.mixin;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import dev.uncandango.alltheleaks.AllTheLeaks;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;

public interface UpdateablePlayer<T> {
   HashMultimap<Class<?>, WeakReference<UpdateablePlayer<?>>> INSTANCES = HashMultimap.create();

   static <O extends UpdateablePlayer<?>> void register(O object) {
      synchronized (INSTANCES) {
         INSTANCES.put(object.getClass(), new WeakReference<>(object));
      }
   }

   void atl$onClientPlayerUpdated(LocalPlayer var1);

   @EventBusSubscriber(Dist.CLIENT)
   class Manager {
      @SubscribeEvent
      public static void onClientClone(Clone event) {
         synchronized (UpdateablePlayer.INSTANCES) {
            Iterator<WeakReference<UpdateablePlayer<?>>> it = UpdateablePlayer.INSTANCES.values().iterator();
            Stopwatch watch = Stopwatch.createStarted();
            int count = 0;

            while (it.hasNext()) {
               count++;
               UpdateablePlayer<?> instance = it.next().get();
               if (instance != null) {
                  instance.atl$onClientPlayerUpdated(event.getNewPlayer());
               } else {
                  it.remove();
               }
            }

            AllTheLeaks.LOGGER.debug("Updated {} player instances in {}ms", count, watch.stop().elapsed(TimeUnit.MILLISECONDS));
         }
      }
   }
}
