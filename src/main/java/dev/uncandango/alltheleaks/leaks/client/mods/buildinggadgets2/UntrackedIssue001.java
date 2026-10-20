package dev.uncandango.alltheleaks.leaks.client.mods.buildinggadgets2;

import com.direwolf20.buildinggadgets2.client.renderer.VBORenderer;
import com.direwolf20.buildinggadgets2.util.FakeRenderingWorld;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "buildinggadgets2", versionRange = "[1.3.7,)")
public class UntrackedIssue001 {
   public static final VarHandle FAKE_RENDERING_WORLD = ReflectionHelper.getFieldFromClass(
      VBORenderer.class, "fakeRenderingWorld", FakeRenderingWorld.class, true
   );
   public static final VarHandle REAL_WORLD = ReflectionHelper.getFieldFromClass(FakeRenderingWorld.class, "realWorld", Level.class, false);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearFakeWorld);
   }

   private void clearFakeWorld(UpdateableLevel.RenderEnginesUpdated event) {
      if (event.getLevel() == null) {
         FAKE_RENDERING_WORLD.set(null);
      } else {
         Object world = (Object)FAKE_RENDERING_WORLD.get();
         if (world != null) {
            REAL_WORLD.set((Object)world, (ClientLevel)event.getLevel());
         }
      }
   }
}
