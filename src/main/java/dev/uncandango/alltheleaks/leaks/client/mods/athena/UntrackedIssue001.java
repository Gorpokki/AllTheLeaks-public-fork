package dev.uncandango.alltheleaks.leaks.client.mods.athena;

import dev.uncandango.alltheleaks.annotation.Issue;
import net.neoforged.fml.ModList;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

@Issue(modId = "athena", versionRange = "[4.0.0,4.0.6)", mixins = "main.AthenaResourceLoaderMixin")
public class UntrackedIssue001 {
   public static boolean isModernFixDynResEnabled() {
      return ModList.get().isLoaded("modernfix") ? ModernFixMixinPlugin.instance.isOptionEnabled("perf.dynamic_resources.BlockModelShaperMixin") : false;
   }
}
