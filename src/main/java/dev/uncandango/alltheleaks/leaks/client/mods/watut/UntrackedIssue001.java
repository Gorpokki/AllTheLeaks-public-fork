package dev.uncandango.alltheleaks.leaks.client.mods.watut;

import com.corosus.watut.PlayerStatusManagerClient;
import com.corosus.watut.client.CustomParticleEngine;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "watut", versionRange = "*")
public class UntrackedIssue001 {
   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::updateLevel);
   }

   private void updateLevel(UpdateableLevel.RenderEnginesUpdated event) {
      PlayerStatusManagerClient.getParticleEngine().setLevel(event.getLevel());
   }

   static {
      MethodHandle dummy = ReflectionHelper.getMethodFromClass(
         PlayerStatusManagerClient.class, "getParticleEngine", MethodType.methodType(CustomParticleEngine.class), true
      );
      MethodHandle var1 = ReflectionHelper.getMethodFromClass(
         CustomParticleEngine.class, "setLevel", MethodType.methodType(void.class, ClientLevel.class), false
      );
   }
}
