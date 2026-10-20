package dev.uncandango.alltheleaks.mixin.core.plugin;

import com.bawnorton.mixinsquared.adjuster.MixinAnnotationAdjusterRegistrar;
import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import dev.uncandango.alltheleaks.leaks.IssueManager;
import dev.uncandango.alltheleaks.mixinsq.ATLMixinAdjuster;
import dev.uncandango.alltheleaks.mixinsq.ATLMixinCanceller;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.stream.Streams;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public class ATLMixinPlugin implements IMixinConfigPlugin {
   private static boolean containsReviveInst(AbstractInsnNode instruction) {
      return instruction instanceof MethodInsnNode mInst ? mInst.name.equals("revive") : false;
   }

   public void onLoad(String mixinPackage) {
      MixinCancellerRegistrar.register(new ATLMixinCanceller());
      MixinAnnotationAdjusterRegistrar.register(new ATLMixinAdjuster());
      DebugThreadsStuck.start();
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      AtomicBoolean result = new AtomicBoolean(IssueManager.getAllowedMixins().contains(mixinClassName.replace("dev.uncandango.alltheleaks.mixin.core.", "")));
      if (result.get() && targetClassName.equals("top.theillusivec4.curios.common.event.CuriosEventHandler")) {
         try {
            ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode("top.theillusivec4.curios.common.event.CuriosEventHandler");
            classNode.methods
               .stream()
               .filter(m -> m.name.equals("playerClone"))
               .findFirst()
               .ifPresent(methodNode -> result.set(Streams.of(methodNode.instructions.toArray()).anyMatch(ATLMixinPlugin::containsReviveInst)));
         } catch (ClassNotFoundException | IOException e) {
            result.set(false);
         }
      }

      return result.get();
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
