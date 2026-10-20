package dev.uncandango.alltheleaks.leaks.client.mods.minecolonies;

import com.google.common.cache.Cache;
import com.minecolonies.core.compatibility.jei.GenericRecipeCategory;
import com.minecolonies.core.compatibility.jei.JobBasedRecipeCategory;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "minecolonies", versionRange = "[1.1.801-1.21.1-snapshot,)", extraModDep = "jei", extraModDepVersions = "[19.16.4.168,)")
public class UntrackedIssue001 {
   public static final VarHandle ENTITY_CACHE = ReflectionHelper.getFieldFromClass(GenericRecipeCategory.class, "entityCache", Cache.class, true);
   public static final VarHandle CITIZEN_CACHE = ReflectionHelper.getFieldFromClass(JobBasedRecipeCategory.class, "citizenCache", Cache.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearOnLevelRefresh);
   }

   private void clearOnLevelRefresh(UpdateableLevel.RenderEnginesUpdated event) {
      (Cache)ENTITY_CACHE.get().invalidateAll();
      (Cache)CITIZEN_CACHE.get().invalidateAll();
   }
}
