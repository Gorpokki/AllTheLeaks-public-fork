package dev.uncandango.alltheleaks.leaks.client.mods.pneumaticcraft;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.mixin.UpdateableLevel;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Map;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@Issue(modId = "pneumaticcraft", versionRange = "[8.0.3,)")
public class UntrackedIssue001 {
   public static final MethodHandle CLEAR_REMOTE_PLAYER = ReflectionHelper.getMethodFromClass(
      CommonArmorHandler.class, "clearHandlerForPlayer", MethodType.methodType(void.class, Player.class), true
   );
   public static final VarHandle CLIENT_HANDLER = ReflectionHelper.getFieldFromClass(CommonArmorHandler.class, "clientHandler", CommonArmorHandler.class, true);
   public static final VarHandle PLAYER_HANDLERS = ReflectionHelper.getFieldFromClass(CommonArmorHandler.class, "playerHandlers", Map.class, false);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearRemotePlayer);
      gameBus.addListener(this::clearClientPlayers);
   }

   private void clearClientPlayers(UpdateableLevel.RenderEnginesUpdated event) {
      (Map)PLAYER_HANDLERS.get((CommonArmorHandler)(CommonArmorHandler)CLIENT_HANDLER.get()).clear();
   }

   private void clearRemotePlayer(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof RemotePlayer player) {
         try {
            CLEAR_REMOTE_PLAYER.invoke((RemotePlayer)player);
         } catch (Throwable e) {
            throw new RuntimeException(e);
         }
      }
   }
}
