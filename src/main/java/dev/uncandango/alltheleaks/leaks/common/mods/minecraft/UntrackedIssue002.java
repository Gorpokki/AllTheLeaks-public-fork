package dev.uncandango.alltheleaks.leaks.common.mods.minecraft;

import dev.uncandango.alltheleaks.annotation.Issue;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

@Issue(modId = "minecraft", versionRange = "1.21.1")
public class UntrackedIssue002 {
   public UntrackedIssue002() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearDamageSource);
   }

   private void clearDamageSource(Post event) {
      if (event.getEntity().tickCount % 41 == 0 && event.getEntity() instanceof LivingEntity living) {
         living.getLastDamageSource();
      }
   }
}
