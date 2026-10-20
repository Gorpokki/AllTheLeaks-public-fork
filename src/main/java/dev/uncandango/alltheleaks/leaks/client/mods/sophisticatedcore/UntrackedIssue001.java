package dev.uncandango.alltheleaks.leaks.client.mods.sophisticatedcore;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ScreenEvent.Closing;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import net.p3pp3rf1y.sophisticatedcore.inventory.StorageWrapperRepository;

@Issue(modId = "sophisticatedcore", versionRange = "[0.6.23,)")
public class UntrackedIssue001 {
   public static final VarHandle CACHE = ReflectionHelper.getFieldFromClass(ItemStackKey.class, "CACHE", Map.class, true);

   public UntrackedIssue001() {
      IEventBus gameBus = NeoForge.EVENT_BUS;
      gameBus.addListener(this::clearItemStackCache);
      gameBus.addListener(this::clearItemStackCacheLevelUnload);
   }

   private void clearItemStackCacheLevelUnload(Unload event) {
      if (event.getLevel().isClientSide()) {
         StorageWrapperRepository.clearCache();
      }
   }

   private void clearItemStackCache(Closing event) {
      if (Minecraft.getInstance().player != null) {
         Map<ItemStack, ItemStackKey> cacheMap = (Map)CACHE.get();
         cacheMap.clear();
      }
   }
}
