package dev.uncandango.alltheleaks.feature.common.mods.minecraft;

import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.Lockable;
import dev.uncandango.alltheleaks.mixin.core.main.accessor.PatchedDataComponentMapAccessor;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.neoforged.fml.loading.LoadingModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Issue(
   modId = "minecraft",
   issueId = "Ingredient Deduplication",
   versionRange = "1.21.1",
   mixins = {
         "main.IngredientMixin",
         "main.IngredientMixin$IngredientAccessor",
         "main.accessor.PatchedDataComponentMapAccessor",
         "main.IngredientLockMixin",
         "main.ItemStackLockMixin"
   },
   config = "ingredientDedupe",
   configActivated = false
)
public class IngredientDedupe implements PreparableReloadListener {
   private static final ObjectOpenCustomHashSet<Ingredient> INGREDIENT_CACHE;
   private static final boolean MODERNFIX_DEDUPLICATION;
   public static IngredientDedupe INSTANCE;

   public static IngredientDedupe getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new IngredientDedupe();
      }

      return INSTANCE;
   }

   private static boolean strictIsSameComponents(ItemStack item1, ItemStack item2) {
      if (!(
         item1.getComponents() instanceof PatchedDataComponentMapAccessor accessor1
            && item2.getComponents() instanceof PatchedDataComponentMapAccessor accessor2
      )) {
         return item1.getComponents() == item2.getComponents();
      } else {
         if (!accessor1.atl$getPrototype().equals(accessor2.atl$getPrototype())) {
            return false;
         }

         Reference2ObjectMap<DataComponentType<?>, Optional<?>> aPatch = accessor1.atl$getPatch();
         Reference2ObjectMap<DataComponentType<?>, Optional<?>> bPatch = accessor2.atl$getPatch();
         if (aPatch != bPatch) {
            if (aPatch.size() != bPatch.size()) {
               return false;
            }

            ObjectIterator var6 = Reference2ObjectMaps.fastIterable(aPatch).iterator();

            while (var6.hasNext()) {
               Entry<DataComponentType<?>, Optional<?>> entry = (Entry<DataComponentType<?>, Optional<?>>)var6.next();
               Optional<?> value = (Optional<?>)bPatch.get(entry.getKey());
               if (value == null) {
                  return false;
               }

               if (value.isPresent() != ((Optional)entry.getValue()).isPresent()) {
                  return false;
               }

               if (value.isPresent() && value.get() != ((Optional)entry.getValue()).get()) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static synchronized Ingredient intern(Ingredient ingredient) {
      Ingredient deduped = (Ingredient)INGREDIENT_CACHE.addOrGet(ingredient);
      if (!((Lockable)deduped).atl$isLocked()) {
         ((Lockable)deduped).atl$setLocked(true);
      }

      return deduped;
   }

   public static synchronized void clearIngredientsWithComponents() {
      int initialSize = INGREDIENT_CACHE.size();
      INGREDIENT_CACHE.removeIf(ingredient -> {
         for (Value value : ingredient.getValues()) {
            if (value instanceof ItemValue $b$0) {
               ItemValue var10000 = $b$0;

               try {
                  var11 = var10000.item();
               } catch (Throwable var9) {
                  throw new MatchException(var9.toString(), var9);
               }

               ItemStack patt1$temp = var11;
               ItemStack item = patt1$temp;
               if (item.getComponents() instanceof PatchedDataComponentMapAccessor accessor && accessor.atl$getPatch() != null) {
                  return !accessor.atl$getPatch().isEmpty();
               }
            }
         }

         return false;
      });
      AllTheLeaks.LOGGER.info("Cleared {} ingredients from Ingredient Dedupe Cache", initialSize - INGREDIENT_CACHE.size());
   }

   public CompletableFuture<Void> reload(
      PreparationBarrier preparationBarrier,
      ResourceManager resourceManager,
      ProfilerFiller preparationsProfiler,
      ProfilerFiller reloadProfiler,
      Executor backgroundExecutor,
      Executor gameExecutor
   ) {
      return CompletableFuture.runAsync(IngredientDedupe::clearIngredientsWithComponents, backgroundExecutor).thenCompose(preparationBarrier::wait);
   }

   public String getName() {
      return "atl_ingredient_dedupe";
   }

   static {
      var BASIC_HASH_STRATEGY = new Strategy<Ingredient>() {
         public int hashCode(Ingredient o) {
            if (o == null) {
               return 0;
            }

            int result = 1;

            for (Object element : o.getValues()) {
               if (element instanceof ItemValue iv) {
                  result = 31 * result + ItemStackLinkedSet.TYPE_AND_TAG.hashCode(iv.item()) + (iv.item().getCount() > 1 ? iv.item().getCount() : 0);
               } else {
                  result = 31 * result + (element == null ? 0 : element.hashCode());
               }
            }

            return result;
         }

         public boolean equals(Ingredient a, Ingredient b) {
            if (b == null) {
               return false;
            }

            Value[] aValues = a.getValues();
            Value[] bValues = b.getValues();
            if (aValues.length != bValues.length) {
               return false;
            }

            for (int i = 0; i < aValues.length; i++) {
               Value aValue = aValues[i];
               Value bValue = bValues[i];
               if (aValue.getClass() != bValue.getClass()) {
                  return false;
               }

               if (aValue instanceof TagValue && bValue instanceof TagValue) {
                  if (!aValue.equals(bValue)) {
                     return false;
                  }
               } else {
                  if (!(aValue instanceof ItemValue var10)) {
                     return false;
                  }

                  ItemValue var10000 = var10;

                  try {
                     var16 = var10000.item();
                  } catch (Throwable var14) {
                     throw new MatchException(var14.toString(), var14);
                  }

                  ItemStack var12 = var16;
                  ItemStack item = var12;
                  if (!(bValue instanceof ItemValue var11)) {
                     return false;
                  }

                  var10000 = var11;

                  try {
                     var18 = var10000.item();
                  } catch (Throwable var13) {
                     throw new MatchException(var13.toString(), var13);
                  }

                  var12 = var18;
                  ItemStack item1 = var12;
                  if (IngredientDedupe.MODERNFIX_DEDUPLICATION) {
                     if (aValue != bValue) {
                        return false;
                     }
                  } else if (item.getCount() != item1.getCount() || !ItemStack.isSameItem(item, item1) || !IngredientDedupe.strictIsSameComponents(item, item1)
                     )
                   {
                     return false;
                  }
               }
            }

            return true;
         }
      };
      INGREDIENT_CACHE = new ObjectOpenCustomHashSet(BASIC_HASH_STRATEGY);
      boolean modernFixDeduplication = false;
      if (LoadingModList.get().getModFileById("modernfix") != null) {
         String versionString = LoadingModList.get().getModFileById("modernfix").versionString();
         DefaultArtifactVersion version = new DefaultArtifactVersion(versionString);

         try {
            VersionRange versionRange = VersionRange.createFromVersionSpec("[5.27.15,)");
            boolean shouldCheckBeta = versionRange.containsVersion(version);
            if (shouldCheckBeta) {
               Method featureLevelMethod = ModernFixMixinPlugin.class.getMethod("activeFeatureLevel");
               boolean isBetaEnabled = ((Enum)featureLevelMethod.invoke(null)).name().equals("BETA");
               if (isBetaEnabled) {
                  modernFixDeduplication = ModernFixMixinPlugin.instance.isOptionEnabled("perf.ingredient_item_deduplication.IngredientMixin");
               }
            } else {
               modernFixDeduplication = ModernFixMixinPlugin.instance.isOptionEnabled("perf.ingredient_item_deduplication.IngredientMixin");
            }
         } catch (Exception e) {
            AllTheLeaks.LOGGER.error("Failed to check ModernFix version", e);
         }
      }

      MODERNFIX_DEDUPLICATION = modernFixDeduplication;
   }
}
