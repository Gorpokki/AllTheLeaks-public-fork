package dev.uncandango.alltheleaks.leaks.client.mods.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.List;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "ftbchunks", versionRange = "*")
public class UntrackedIssue001 {
   public static final VarHandle MAP_ICONS = ReflectionHelper.getFieldFromClass(FTBChunksClient.class, "mapIcons", List.class, false);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearMapIcons);
   }

   private void clearMapIcons(UpdateableLevel.RenderEnginesUpdated event) {
      (List)MAP_ICONS.get((FTBChunksClient)FTBChunksClient.INSTANCE).clear();
   }

   static {
      FTBChunksClient dummy = FTBChunksClient.INSTANCE;
   }
}
