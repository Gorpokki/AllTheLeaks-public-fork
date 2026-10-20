package dev.uncandango.alltheleaks.mixinsq;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.AdjustableOverwriteNode;
import com.bawnorton.mixinsquared.adjuster.tools.type.MethodListAnnotationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import com.google.common.collect.Sets;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.CompatibleHashes;
import dev.uncandango.alltheleaks.utils.MethodNodeHasher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.common.util.Lazy;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.service.MixinService;

public class ATLMixinAdjuster implements MixinAnnotationAdjuster {
   private static final AdjustableAnnotationNode UNIQUE = AdjustableAnnotationNode.fromNode(new AnnotationNode(Unique.class.descriptorString()));
   private static final String ATL_PREFIX = "dev.uncandango.alltheleaks.mixin.core.";
   private static final Map<String, Lazy<Integer>> hashCodes = new ConcurrentHashMap<>();
   private static final Map<MethodNode, List<Integer>> compatibleHashes = new ConcurrentHashMap<>();
   private static final Set<String> computingHashes = Sets.newConcurrentHashSet();

   public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode, AdjustableAnnotationNode annotationNode) {
      return mixinClassName.startsWith("dev.uncandango.alltheleaks.mixin.core.")
         ? processHashes(targetClassNames, mixinClassName, annotationNode, handlerNode)
         : annotationNode;
   }

   private boolean isModernfixFeatureOn(String myMixin, String currentMixin, String modernfixMixin) {
      return ("dev.uncandango.alltheleaks.mixin.core." + myMixin).equals(currentMixin) && LoadingModList.get().getModFileById("modernfix") != null
         ? ModernFixMixinPlugin.instance.isOptionEnabled(modernfixMixin)
         : false;
   }

   private boolean isATLFeatureOn(Set<String> myMixin, String currentMixin, boolean feature) {
      return myMixin.stream().anyMatch(mixin -> ("dev.uncandango.alltheleaks.mixin.core." + mixin).equals(currentMixin)) ? feature : false;
   }

   private static synchronized AdjustableAnnotationNode processHashes(
      List<String> targetClassNames, String mixinClassName, AdjustableAnnotationNode annotationNode, MethodNode handlerNode
   ) {
      if (annotationNode.desc.equals(CompatibleHashes.class.descriptorString())) {
         if (targetClassNames.size() > 1) {
            throw new IllegalArgumentException("CompatibleHashes can't be applied to multiple target classes!");
         }

         annotationNode.get("values")
            .ifPresentOrElse(hashes -> compatibleHashes.put(handlerNode, (List<Integer>)hashes), () -> compatibleHashes.put(handlerNode, List.of(0)));
         computingHashes.add(mixinClassName);

         try {
            ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(targetClassNames.get(0));

            for (MethodNode method : classNode.methods) {
               Lazy<Integer> calcHash = Lazy.of(() -> MethodNodeHasher.hash(classNode, method.name + method.desc));
               String fullName = method.name + method.desc;
               hashCodes.put(fullName, calcHash);
            }

            return annotationNode;
         } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         if (!computingHashes.contains(mixinClassName)) {
            return annotationNode;
         }

         List<String> methods = null;
         if (annotationNode instanceof MethodListAnnotationNode methodListAnnotationNode) {
            methods = methodListAnnotationNode.getMethod();
         }

         if (annotationNode instanceof AdjustableOverwriteNode) {
            methods = new ArrayList<>(List.of(handlerNode.name + handlerNode.desc));
         }

         if (methods == null) {
            return annotationNode;
         }

         Iterator<String> it = methods.iterator();

         while (it.hasNext()) {
            String method = it.next();
            String searchMethod = method.startsWith("L") && method.indexOf(";m_") > 0
               ? method.substring(method.indexOf(";m_") + 1)
               : (method.indexOf(40) == -1 ? method + "(" : method);
            if (searchMethod.indexOf(42) > 0) {
               searchMethod = searchMethod.replace("*", "");
            }

            boolean found = false;

            for (String key : hashCodes.keySet()) {
               if (key.startsWith(searchMethod)) {
                  found = true;
                  Integer hash = (Integer)hashCodes.get(key).get();
                  AllTheLeaks.LOGGER.debug("Method Hash {} calculated for method {} at mixin {}", new Object[]{hash, method, mixinClassName});
                  List<Integer> expectedHashes = compatibleHashes.get(handlerNode);
                  if (!expectedHashes.contains(hash)) {
                     AllTheLeaks.LOGGER
                        .info(
                           "Method {} at mixin {} cancelled due to not matching hash: Actual {}, Expected: {}",
                           new Object[]{method, mixinClassName, hash, expectedHashes}
                        );
                     it.remove();
                     break;
                  }
               }
            }

            if (!found) {
               IllegalArgumentException ex = new IllegalArgumentException(
                  "Method " + searchMethod + " not found in possible methods of the class " + targetClassNames.get(0)
               );
               AllTheLeaks.LOGGER.error(ex.getMessage(), ex);
               methods.clear();
            }
         }

         hashCodes.clear();
         compatibleHashes.clear();
         computingHashes.remove(mixinClassName);
         if (methods.isEmpty()) {
            AllTheLeaks.LOGGER.info("Mixin annotation {} at mixin {} removed due to no methods available", annotationNode, mixinClassName);
            return UNIQUE;
         } else {
            return annotationNode;
         }
      }
   }
}
