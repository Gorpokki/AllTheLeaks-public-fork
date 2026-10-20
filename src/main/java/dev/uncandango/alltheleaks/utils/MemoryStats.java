package dev.uncandango.alltheleaks.utils;

import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.api.windows.ProcessMemoryCounter;
import dev.uncandango.alltheleaks.api.windows.PsApi;
import java.util.Locale;
import net.minecraft.Util;
import net.neoforged.fml.loading.FMLEnvironment;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.Kernel32;

public class MemoryStats {
   public static final boolean ENABLED;
   public static long lastUpdateTime = Util.getMillis();
   public static volatile String memoryString = "OS Used Memory: Fetching...";

   public static String getMemoryWorkingSetSize() {
      long timeNow = Util.getMillis();
      if (timeNow - lastUpdateTime > 1000L) {
         lastUpdateTime = timeNow;
         memoryString = String.format(Locale.ROOT, "OS Used Memory: %03dMB", fetchMemorySize() / 1024L / 1024L);
      }

      return memoryString;
   }

   private static long fetchMemorySize() {
      MemoryStack stack = MemoryStack.stackPush();

      long var2;
      try {
         ProcessMemoryCounter report = ProcessMemoryCounter.malloc(stack);
         PsApi.GetProcessMemoryInfo(Kernel32.GetCurrentProcess(), report.address(), report.sizeof());
         var2 = report.workingSetSize();
      } catch (Throwable var5) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stack != null) {
         stack.close();
      }

      return var2;
   }

   static {
      boolean status = false;
      if (!FMLEnvironment.dist.isDedicatedServer()) {
         status = Platform.get() == Platform.WINDOWS;
         if (status) {
            try {
               PsApi.getLibrary();
               Kernel32.GetCurrentProcess();
            } catch (Throwable e) {
               AllTheLeaks.LOGGER.error("Error while instancing PsApi: {}", e.getMessage());
               status = false;
            }
         }
      }

      ENABLED = status;
   }
}
