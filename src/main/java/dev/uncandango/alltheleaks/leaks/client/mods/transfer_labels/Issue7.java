package dev.uncandango.alltheleaks.leaks.client.mods.transfer_labels;

import com.buuz135.transfer_labels.storage.client.LabelClientStorage;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import java.util.HashMap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "transfer_labels", versionRange = "[0.1.0,0.1.7)")
public class Issue7 {
   public Issue7() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearMapOnRenderUpdate);
   }

   private void clearMapOnRenderUpdate(UpdateableLevel.RenderEnginesUpdated event) {
      LabelClientStorage.LABELS.clear();
   }

   static {
      HashMap var0 = LabelClientStorage.LABELS;
   }
}
