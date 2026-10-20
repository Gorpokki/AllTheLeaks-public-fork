package dev.uncandango.alltheleaks.leaks.client.mods.geckolib;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.loading.math.MolangQueries.Actor;

@Issue(modId = "geckolib", versionRange = "[4.6,4.6.2]", mixins = "main.GeoArmorRendererMixin")
public class Issue625 {
   private static final VarHandle ACTOR = ReflectionHelper.getFieldFromClass(MolangQueries.class, "ACTOR", Actor.class, true);

   public Issue625() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearActor);
   }

   private void clearActor(UpdateableLevel.RenderEnginesUpdated event) {
      ACTOR.set(null);
   }
}
