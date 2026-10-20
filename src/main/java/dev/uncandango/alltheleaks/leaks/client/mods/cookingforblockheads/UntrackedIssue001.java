package dev.uncandango.alltheleaks.leaks.client.mods.cookingforblockheads;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.blay09.mods.cookingforblockheads.client.render.CowJarRenderer;
import net.minecraft.world.entity.animal.Cow;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "cookingforblockheads", versionRange = "[21.1.14,)")
public class UntrackedIssue001 {
   public static final VarHandle ENTITY = ReflectionHelper.getFieldFromClass(CowJarRenderer.class, "entity", Cow.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearEntities);
   }

   private void clearEntities(UpdateableLevel.RenderEnginesUpdated event) {
      ENTITY.set(null);
   }
}
