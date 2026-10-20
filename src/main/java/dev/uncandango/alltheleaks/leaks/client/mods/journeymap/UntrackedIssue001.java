package dev.uncandango.alltheleaks.leaks.client.mods.journeymap;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.EntityComparatorAccessor;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import journeymap.client.model.EntityHelper;

@Issue(
   modId = "journeymap",
   versionRange = "[1.21.1-6.0.0-beta.28,1.21.1-6.0.0-beta.32]",
   mixins = {"main.accessor.EntityComparatorAccessor", "main.EntityHelperMixin"}
)
public class UntrackedIssue001 {
   private static final VarHandle entityDistanceComparator = ReflectionHelper.getFieldFromClass(
      EntityHelper.class, "entityDistanceComparator", UntrackedIssue001.edcClass, true
   );
   private static final VarHandle entityDTODistanceComparator = ReflectionHelper.getFieldFromClass(
      EntityHelper.class, "entityDTODistanceComparator", UntrackedIssue001.edtoClass, true
   );
   private static final Class<?> edcClass = ReflectionHelper.getPrivateClass(
      EntityHelper.class, "journeymap.client.model.EntityHelper$EntityDistanceComparator"
   );
   private static final Class<?> edtoClass = ReflectionHelper.getPrivateClass(
      EntityHelper.class, "journeymap.client.model.EntityHelper$EntityDTODistanceComparator"
   );

   public static void clearPlayerFromComparator() {
      if (ReflectionHelper.getFieldValue(entityDistanceComparator) instanceof EntityComparatorAccessor accessor) {
         accessor.atl$setPlayer(null);
      }
   }

   public static void clearPlayerFromDTOComparator() {
      if (ReflectionHelper.getFieldValue(entityDTODistanceComparator) instanceof EntityComparatorAccessor accessor) {
         accessor.atl$setPlayer(null);
      }
   }
}
