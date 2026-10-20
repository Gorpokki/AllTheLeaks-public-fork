package dev.uncandango.alltheleaks.api.windows;

import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct PROCESS_MEMORY_COUNTERS")
public class ProcessMemoryCounter extends Struct<ProcessMemoryCounter> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int CB;
   public static final int PAGE_FAULT_COUNT;
   public static final int PEAK_WORKING_SET_SIZE;
   public static final int WORKING_SET_SIZE;
   public static final int QUOTA_PEAK_PAGED_POOL_USAGE;
   public static final int QUOTA_PAGED_POOL_USAGE;
   public static final int QUOTA_PEAK_NON_PAGED_POOL_USAGE;
   public static final int QUOTA_NON_PAGED_POOL_USAGE;
   public static final int PAGEFILE_USAGE;
   public static final int PEAK_PAGEFILE_USAGE;

   protected ProcessMemoryCounter(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   public ProcessMemoryCounter(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   protected ProcessMemoryCounter create(long address, @Nullable ByteBuffer container) {
      return new ProcessMemoryCounter(address, container);
   }

   public static ProcessMemoryCounter create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new ProcessMemoryCounter(MemoryUtil.memAddress(container), container);
   }

   public static ProcessMemoryCounter malloc(MemoryStack stack) {
      return new ProcessMemoryCounter(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public int sizeof() {
      return SIZEOF;
   }

   public int cb() {
      return ncb(this.address());
   }

   public static int ncb(long struct) {
      return UNSAFE.getInt(null, struct + CB);
   }

   public int pageFaultCount() {
      return npageFaultCount(this.address());
   }

   public static int npageFaultCount(long struct) {
      return UNSAFE.getInt(null, struct + PAGE_FAULT_COUNT);
   }

   public long peakWorkingSetSize() {
      return npeakWorkingSetSize(this.address());
   }

   public static long npeakWorkingSetSize(long struct) {
      return MemoryUtil.memGetAddress(struct + PEAK_WORKING_SET_SIZE);
   }

   public long workingSetSize() {
      return nworkingSetSize(this.address());
   }

   public static long nworkingSetSize(long struct) {
      return MemoryUtil.memGetAddress(struct + WORKING_SET_SIZE);
   }

   public long quotaPeakPagedPoolUsage() {
      return nquotaPeakPagedPoolUsage(this.address());
   }

   public static long nquotaPeakPagedPoolUsage(long struct) {
      return MemoryUtil.memGetAddress(struct + QUOTA_PEAK_PAGED_POOL_USAGE);
   }

   public long quotaPagedPoolUsage() {
      return nquotaPagedPoolUsage(this.address());
   }

   public static long nquotaPagedPoolUsage(long struct) {
      return MemoryUtil.memGetAddress(struct + QUOTA_PAGED_POOL_USAGE);
   }

   public long quotaPeakNonPagedPoolUsage() {
      return nquotaPeakNonPagedPoolUsage(this.address());
   }

   public static long nquotaPeakNonPagedPoolUsage(long struct) {
      return MemoryUtil.memGetAddress(struct + QUOTA_PEAK_NON_PAGED_POOL_USAGE);
   }

   public long quotaNonPagedPoolUsage() {
      return nquotaNonPagedPoolUsage(this.address());
   }

   public static long nquotaNonPagedPoolUsage(long struct) {
      return MemoryUtil.memGetAddress(struct + QUOTA_NON_PAGED_POOL_USAGE);
   }

   public long pagefileUsage() {
      return npagefileUsage(this.address());
   }

   public static long npagefileUsage(long struct) {
      return MemoryUtil.memGetAddress(struct + PAGEFILE_USAGE);
   }

   public long peakPagefileUsage() {
      return npeakPagefileUsage(this.address());
   }

   public static long npeakPagefileUsage(long struct) {
      return MemoryUtil.memGetAddress(struct + PEAK_PAGEFILE_USAGE);
   }

   static {
      Layout layout = __struct(
         new Member[]{
            __member(4),
            __member(4),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE)
         }
      );
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      CB = layout.offsetof(0);
      PAGE_FAULT_COUNT = layout.offsetof(1);
      PEAK_WORKING_SET_SIZE = layout.offsetof(2);
      WORKING_SET_SIZE = layout.offsetof(3);
      QUOTA_PEAK_PAGED_POOL_USAGE = layout.offsetof(4);
      QUOTA_PAGED_POOL_USAGE = layout.offsetof(5);
      QUOTA_PEAK_NON_PAGED_POOL_USAGE = layout.offsetof(6);
      QUOTA_NON_PAGED_POOL_USAGE = layout.offsetof(7);
      PAGEFILE_USAGE = layout.offsetof(8);
      PEAK_PAGEFILE_USAGE = layout.offsetof(9);
   }
}
