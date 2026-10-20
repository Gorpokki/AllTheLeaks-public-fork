package dev.uncandango.alltheleaks.leaks.common.mods.evilcraft;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import org.cyclops.evilcraft.enchantment.entityeffect.EnchantmentEntityEffectHealFromDamage;

@Issue(modId = "evilcraft", issueId = "#1051", versionRange = "[1.2.50,)")
public class Issue1051 {
   private static final VarHandle LAST_DAMAGE_EVENT = ReflectionHelper.getFieldFromClass(
      EnchantmentEntityEffectHealFromDamage.class, "lastDamageEvent", Post.class, true
   );

   public Issue1051() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearDamageEvent);
      gameBus.addListener(this::clearDamageEventOnClone);
   }

   private void clearDamageEvent(Unload event) {
      if (!event.getLevel().isClientSide()) {
         Post postEvent = (Post)LAST_DAMAGE_EVENT.get();
         if (postEvent != null) {
            if (postEvent.getEntity().isRemoved()) {
               LAST_DAMAGE_EVENT.set(null);
            }

            if (postEvent.getSource().getEntity() != null && postEvent.getSource().getEntity().isRemoved()) {
               LAST_DAMAGE_EVENT.set(null);
            }
         }
      }
   }

   private void clearDamageEventOnClone(Clone event) {
      if (!event.getEntity().level().isClientSide()) {
         Post postEvent = (Post)LAST_DAMAGE_EVENT.get();
         if (postEvent != null) {
            if (postEvent.getEntity().isRemoved()) {
               LAST_DAMAGE_EVENT.set(null);
            }

            if (postEvent.getSource().getEntity() != null && postEvent.getSource().getEntity().isRemoved()) {
               LAST_DAMAGE_EVENT.set(null);
            }
         }
      }
   }
}
