package dev.uncandango.alltheleaks.mixin;

public interface InnerLockable extends Lockable {
   boolean atl$isInnerLocked();

   void atl$setInnerLocked(boolean var1);

   @Override
   boolean atl$isLocked();

   @Override
   void atl$setLocked(boolean var1);
}
