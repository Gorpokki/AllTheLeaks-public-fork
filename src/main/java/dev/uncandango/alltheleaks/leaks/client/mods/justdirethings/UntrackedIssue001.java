package dev.uncandango.alltheleaks.leaks.client.mods.justdirethings;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

@Issue(modId = "justdirethings", versionRange = "[1.5.1,)")
public class UntrackedIssue001 {
   public static final VarHandle MOCK_PLAYER;

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearPlayerOnRenderEngineUpdate);
   }

   private void clearPlayerOnRenderEngineUpdate(UpdateableLevel.RenderEnginesUpdated event) {
      MOCK_PLAYER.set(null);
   }

   static {
      Class<?> clazz = ReflectionHelper.getClass("com.direwolf20.justdirethings.client.blockentityrenders.InventoryHolderBER");
      MOCK_PLAYER = ReflectionHelper.getFieldFromClass(clazz, "mockPlayer", AbstractClientPlayer.class, true);
   }
}
