package dev.uncandango.alltheleaks.leaks.client.mods.reliquary;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import reliquary.util.TooltipBuilder;

@Issue(modId = "reliquary", versionRange = "[2.0.44,)")
public class UntrackedIssue001 {
   public static final VarHandle CONTEXT = ReflectionHelper.getFieldFromClass(TooltipBuilder.class, "context", TooltipContext.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::refreshContext);
      gameBus.addListener(this::clearContext);
   }

   private void refreshContext(Load event) {
      if (event.getLevel().isClientSide()) {
         CONTEXT.set((TooltipContext)TooltipContext.of((Level)event.getLevel()));
      }
   }

   private void clearContext(Unload event) {
      if (event.getLevel().isClientSide()) {
         CONTEXT.set((TooltipContext)TooltipContext.EMPTY);
      }
   }
}
