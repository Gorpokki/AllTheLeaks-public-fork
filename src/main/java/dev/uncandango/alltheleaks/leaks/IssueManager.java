package dev.uncandango.alltheleaks.leaks;

import com.google.common.collect.Sets;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.AnnotationHelper;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.config.ATLProperties;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

public class IssueManager {
   private static final List<String> constructors = new ArrayList<>();
   private static Set<String> mixinAllowed;
   private static final List<AnnotationData> annotations = new ArrayList<>();
   private static Set<String> mixinToCancel;

   public static Set<String> getMixinToCancel() {
      if (mixinToCancel == null) {
         getAllowedMixins();
      }

      return mixinToCancel;
   }

   public static Set<String> getAllowedMixins() {
      if (mixinAllowed == null) {
         mixinAllowed = Sets.newHashSet();
         mixinToCancel = Sets.newHashSet();
         ModFileScanData scanData = LoadingModList.get().getModFileById("alltheleaks").getFile().getScanResult();
         Dist currentDist = FMLEnvironment.dist;
         scanData.getAnnotatedBy(Issue.class, ElementType.TYPE).forEach(annotation -> {
            if (AllTheLeaks.INDEV) {
               annotations.add(annotation);
            }

            String issueId = AnnotationHelper.getValue(annotation, "issueId");
            String modId = AnnotationHelper.getValue(annotation, "modId");
            String flag = AnnotationHelper.getValue(annotation, "config");
            String modAbsent = AnnotationHelper.getValue(annotation, "onlyIfModAbsent");
            boolean flagActivated;
            if (Objects.equals(flag, "")) {
               flagActivated = true;
            } else {
               try {
                  Field configValue = ATLProperties.class.getField(flag);
                  flagActivated = (Boolean)configValue.get(ATLProperties.get());
               } catch (NoSuchFieldException | IllegalAccessException e) {
                  AllTheLeaks.LOGGER.warn("Config property {} does not exist!", flag);
                  return;
               }
            }

            if (!flagActivated) {
               AllTheLeaks.LOGGER.debug("Skipping feature {} from mod {} as it's feature flag is not activated!", issueId, modId);
            } else if (modAbsent != null && LoadingModList.get().getModFileById(modAbsent) != null) {
               AllTheLeaks.LOGGER.debug("Skipping issue {} from mod {} as mod {} is present!", new Object[]{issueId, modId, modAbsent});
            } else {
               Boolean devOnly = AnnotationHelper.getValue(annotation, "devOnly");
               if (devOnly && !AllTheLeaks.INDEV) {
                  AllTheLeaks.LOGGER.debug("Skipping issue {} from mod {} as it's dev only!", issueId, modId);
               } else {
                  Boolean solved = AnnotationHelper.getValue(annotation, "solved");
                  List<String> extraModDep = AnnotationHelper.getValue(annotation, "extraModDep");
                  List<String> extraModDepVersions = AnnotationHelper.getValue(annotation, "extraModDepVersions");
                  if (!currentDist.isDedicatedServer() && annotation.memberName().contains(".server.mods.")) {
                     AllTheLeaks.LOGGER.debug("Skipping issue {} from mod {} as it is server side only!", issueId, modId);
                  } else {
                     if (currentDist.isDedicatedServer()) {
                        Dist side = annotation.memberName().contains(".client.mods.") ? Dist.CLIENT : Dist.DEDICATED_SERVER;
                        if (side.isClient()) {
                           AllTheLeaks.LOGGER.debug("Skipping issue {} from mod {} as it is client side only!", issueId, modId);
                           return;
                        }
                     }

                     if (solved) {
                        AllTheLeaks.LOGGER.debug("Skipping issue {} from mod {} as it has been solved by mod!", issueId, modId);
                     } else {
                        String versionRange = AnnotationHelper.getValue(annotation, "versionRange");
                        List<String> mixins = AnnotationHelper.getValue(annotation, "mixins");
                        List<String> mixinsToCancel = AnnotationHelper.getValue(annotation, "mixinsToCancel");
                        Supplier<Boolean> condition = generateCondition(modId, versionRange, annotation.memberName(), extraModDep, extraModDepVersions);
                        if (condition.get()) {
                           if (mixins != null && !mixins.isEmpty()) {
                              AllTheLeaks.LOGGER.info("Mixins added to allowed list: {}", mixins);
                              mixinAllowed.addAll(mixins);
                           }

                           if (mixinsToCancel != null && !mixinsToCancel.isEmpty()) {
                              AllTheLeaks.LOGGER.info("Mixins added to cancel list: {}", mixinsToCancel);
                              mixinToCancel.addAll(mixinsToCancel);
                           }
                        }
                     }
                  }
               }
            }
         });
      }

      return mixinAllowed;
   }

   public static void initiateIssues() {
      constructors.forEach(ctor -> {
         try {
            Class<?> clazz = Class.forName(ctor);
            Method instanceMethod = null;

            try {
               instanceMethod = clazz.getDeclaredMethod("getInstance");
            } catch (NoSuchMethodException var4) {
            }

            if (instanceMethod != null) {
               instanceMethod.invoke(null);
            } else {
               Constructor<?> ctorClazz = clazz.getDeclaredConstructors()[0];
               ctorClazz.newInstance();
            }
         } catch (Throwable e) {
            AllTheLeaks.LOGGER.error("Failed to instantiate constructor.", e);
         }
      });
   }

   public static Supplier<Boolean> generateCondition(
      String modId, String versionRange, String annotatedClass, List<String> extraModDep, List<String> extraModDepVersions
   ) {
      return () -> {
         ModFileInfo mod = LoadingModList.get().getModFileById(modId);
         if (mod != null) {
            try {
               VersionRange range = VersionRange.createFromVersionSpec(versionRange);
               String modVerString = mod.versionString();
               DefaultArtifactVersion modVer = new DefaultArtifactVersion(modVerString);
               if (range.containsVersion(modVer) || modVerString.equals("0.0NONE")) {
                  if (!extraModDep.isEmpty()) {
                     for (int i = 0; i < extraModDep.size(); i++) {
                        ModFileInfo dep = LoadingModList.get().getModFileById(extraModDep.get(i));
                        if (dep == null) {
                           AllTheLeaks.LOGGER.debug("Extra dependency Mod {} is not present in the mod list", extraModDep.get(i));
                           AllTheLeaks.LOGGER.debug("Class {} will NOT be loaded as extra mod dependecy is not present.", annotatedClass);
                           return false;
                        }

                        VersionRange rangeDepVer = VersionRange.createFromVersionSpec(extraModDepVersions.get(i));
                        String depVerString = dep.versionString();
                        DefaultArtifactVersion depVer = new DefaultArtifactVersion(depVerString);
                        if (!rangeDepVer.containsVersion(depVer) && !depVerString.equals("0.0NONE")) {
                           AllTheLeaks.LOGGER
                              .debug(
                                 "Extra dependecy Mod {} does NOT matches versions: {} in {}",
                                 new Object[]{extraModDep.get(i), dep.versionString(), extraModDepVersions.get(i)}
                              );
                           AllTheLeaks.LOGGER.debug("Class {} will NOT be loaded as extra mod dependecy does not match.", annotatedClass);
                           return false;
                        }

                        AllTheLeaks.LOGGER
                           .info(
                              "Extra dependecy Mod {} matches versions: {} in {}",
                              new Object[]{extraModDep.get(i), dep.versionString(), extraModDepVersions.get(i)}
                           );
                     }
                  }

                  constructors.add(annotatedClass);
                  AllTheLeaks.LOGGER.info("Class {} will be loaded as it matches versions: {} in {}", new Object[]{annotatedClass, modVer, range});
                  return true;
               }

               AllTheLeaks.LOGGER
                  .debug("Class {} will NOT be loaded as mod {} does not match versions: {} in {}", new Object[]{annotatedClass, modId, modVer, range});
            } catch (Exception e) {
               AllTheLeaks.LOGGER.error("Error while comparing versions and instantiating class", e);
            }
         } else {
            AllTheLeaks.LOGGER.debug("Class {} will NOT be loaded as mod {} is not present", annotatedClass, modId);
         }

         return false;
      };
   }
}
