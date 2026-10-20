package dev.uncandango.alltheleaks.leaks.client.mods.trophymanager;

import cy.jdkdigital.trophymanager.common.blockentity.TrophyBlockEntity;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "trophymanager", versionRange = "[1.21.1-2.1.8,1.21.1-2.2.4]")
public class UntrackedIssue001 {
   public static final VarHandle CACHED_ENTITIES = ReflectionHelper.getFieldFromClass(TrophyBlockEntity.class, "cachedEntities", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearTrophyManagerCache);
   }

   private void clearTrophyManagerCache(Unload event) {
      (Map)CACHED_ENTITIES.get().entrySet().removeIf(entry -> entry.getValue() != null && ((Entity)entry.getValue()).level() == event.getLevel());
   }
}
