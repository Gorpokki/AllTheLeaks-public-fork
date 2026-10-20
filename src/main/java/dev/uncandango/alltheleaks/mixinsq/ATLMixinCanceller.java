package dev.uncandango.alltheleaks.mixinsq;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.common.util.Lazy;

public class ATLMixinCanceller implements MixinCanceller {
   public static final Lazy<Set<String>> cancelList = Lazy.of(() -> {
      Set<String> list = new HashSet<>();
      if (isLoaded("additionalentityattributes")) {
         list.add("de.dafuqs.additionalentityattributes.mixin.common.BlockMixin");
      }

      return list;
   });

   private static boolean isLoaded(String modId) {
      LoadingModList modlist = LoadingModList.get();
      return modlist == null ? false : modlist.getModFileById(modId) != null;
   }

   public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
      return ((Set)cancelList.get()).contains(mixinClassName);
   }
}
