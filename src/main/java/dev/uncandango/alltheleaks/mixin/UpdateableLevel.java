package dev.uncandango.alltheleaks.mixin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

public interface UpdateableLevel<T> {
   List<WeakReference<UpdateableLevel<?>>> INSTANCES = new ArrayList<>();

   static <O extends UpdateableLevel<?>> void register(O object) {
      synchronized (INSTANCES) {
         INSTANCES.add(new WeakReference<>(object));
      }
   }

   void atl$onClientLevelUpdated(@Nullable ClientLevel var1);

   @EventBusSubscriber(Dist.CLIENT)
   class Manager {
      @SubscribeEvent
      public static void onLevelLoad(UpdateableLevel.RenderEnginesUpdated event) {
         synchronized (UpdateableLevel.INSTANCES) {
            Iterator<WeakReference<UpdateableLevel<?>>> it = UpdateableLevel.INSTANCES.iterator();

            while (it.hasNext()) {
               UpdateableLevel<?> instance = it.next().get();
               if (instance != null) {
                  instance.atl$onClientLevelUpdated(event.getLevel());
               } else {
                  it.remove();
               }
            }

            ((ArrayList)UpdateableLevel.INSTANCES).trimToSize();
         }
      }
   }

   class RenderEnginesUpdated extends Event {
      @Nullable
      ClientLevel level;

      public RenderEnginesUpdated(@Nullable ClientLevel level) {
         this.level = level;
      }

      @Nullable
      public ClientLevel getLevel() {
         return this.level;
      }
   }
}
