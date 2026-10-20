package dev.uncandango.alltheleaks.api.windows;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.windows.Kernel32;

public class PsApi {
   private static final SharedLibrary PSAPI = APIUtil.apiCreateLibrary("psapi");

   public static SharedLibrary getLibrary() {
      return PSAPI;
   }

   @NativeType("BOOL")
   private static boolean nEmptyWorkingSet(@NativeType("HANDLE") long hProcess) {
      long __functionAddress = PsApi.Functions.EmptyWorkingSet;
      if (Checks.CHECKS) {
         Checks.check(__functionAddress);
         Checks.check(hProcess);
      }

      return JNI.callPI(hProcess, __functionAddress) != 0;
   }

   public static boolean EmptyWorkingSetOfCurrentProcess() {
      return nEmptyWorkingSet(Kernel32.GetCurrentProcess());
   }

   @NativeType("BOOL")
   public static boolean GetProcessMemoryInfo(
      @NativeType("HANDLE") long Process, @NativeType("PROCESS_MEMORY_COUNTERS *") long ppsmemCounters, @NativeType("DWORD") int cb
   ) {
      long __functionAddress = PsApi.Functions.GetProcessMemoryInfo;
      if (Checks.CHECKS) {
         Checks.check(__functionAddress);
         Checks.check(Process);
         Checks.check(ppsmemCounters);
      }

      return JNI.callPPPI(Process, ppsmemCounters, cb, __functionAddress) != 0;
   }

   public static final class Functions {
      public static final long EmptyWorkingSet = APIUtil.apiGetFunctionAddress(PsApi.PSAPI, "EmptyWorkingSet");
      public static final long GetProcessMemoryInfo = APIUtil.apiGetFunctionAddress(PsApi.PSAPI, "GetProcessMemoryInfo");

      private Functions() {
      }
   }
}
