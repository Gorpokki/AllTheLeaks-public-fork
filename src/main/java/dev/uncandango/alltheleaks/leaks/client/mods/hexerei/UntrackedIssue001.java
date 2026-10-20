package dev.uncandango.alltheleaks.leaks.client.mods.hexerei;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.VarHandle;
import net.joefoxe.hexerei.data.books.BookEntity;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.CachedMap;
import net.minecraft.world.entity.Entity;

@Issue(modId = "hexerei", versionRange = "[0.5,)", mixins = {"main.BookEntityMixin", "main.BroomItemMixin"})
public class UntrackedIssue001 {
   static {
      VarHandle dummy = ReflectionHelper.getFieldFromClass(BookEntity.class, "entity", Entity.class, false);
      VarHandle dummy2 = ReflectionHelper.getFieldFromClass(BookEntity.class, "markedForUpdate", boolean.class, false);
      VarHandle var2 = ReflectionHelper.getFieldFromClass(BroomItem.class, "cachedBroom", CachedMap.class, false);
   }
}
