package dev.uncandango.alltheleaks.leaks.client.mods.journeymap;

import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import journeymap.client.render.draw.MobIconCache;

@Issue(modId = "journeymap", versionRange = "[1.21.1-6.0.0-beta.28,1.21.1-6.0.0-beta.32]", mixins = "main.ImageSetMixin")
public class UntrackedIssue002 {
   public static final MethodHandle REMOVE;

   static {
      Class<?> privateClass = ReflectionHelper.getPrivateClass(MobIconCache.class, "journeymap.client.render.draw.MobIconCache$IconTexture");
      REMOVE = ReflectionHelper.getMethodFromClass(privateClass, "remove", MethodType.methodType(void.class), false);
   }
}
