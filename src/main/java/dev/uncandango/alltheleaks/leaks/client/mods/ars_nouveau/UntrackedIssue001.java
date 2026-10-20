package dev.uncandango.alltheleaks.leaks.client.mods.ars_nouveau;

import com.hollingsworth.arsnouveau.client.gui.GuiEntityInfoHUD;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

@Issue(modId = "ars_nouveau", versionRange = "[5.0.12,)")
public class UntrackedIssue001 {
   public static final VarHandle LAST_HOVERED = ReflectionHelper.getFieldFromClass(GuiEntityInfoHUD.class, "lastHovered", Object.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearHovered);
      gameBus.addListener(this::clearHoveredOnClone);
   }

   private void clearHovered(Unload event) {
      if (event.getLevel().isClientSide()) {
         LAST_HOVERED.set(null);
      }
   }

   private void clearHoveredOnClone(Clone event) {
      LAST_HOVERED.set(null);
   }
}
