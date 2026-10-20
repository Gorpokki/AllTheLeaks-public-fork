package dev.uncandango.alltheleaks.leaks.common.mods.neoforge;

import com.mojang.datafixers.util.Pair;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.jarhandling.impl.JarContentsImpl;
import cpw.mods.niofs.union.UnionFileSystem;
import cpw.mods.niofs.union.UnionFileSystemProvider;
import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import java.io.IOException;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.system.MemoryUtil;
import sun.misc.Unsafe;

@Issue(
   issueId = "#289",
   modId = "neoforge",
   versionRange = "[21.,21.1.182]",
   description = "Memory leak on FancyModLoader https://github.com/neoforged/FancyModLoader/issues/289"
)
public class Issue289 {
   public Issue289() {
      IEventBus modEventBus = ((ModContainer)ModList.get().getModContainerById("alltheleaks").get()).getEventBus();
      modEventBus.addListener(this::closeDanglingJars);
   }

   private void closeDanglingJars(FMLLoadCompleteEvent event) {
      event.enqueueWork(
         () -> {
            Map<String, UnionFileSystem> copyFileSystems = new HashMap<>();
            Set<FileSystem> validFS = new HashSet<>();

            try {
               Unsafe UNSAFE = (Unsafe)ObfuscationReflectionHelper.getPrivateValue(MemoryUtil.class, null, "UNSAFE");
               if (UNSAFE == null) {
                  throw new IllegalAccessException("Not possible to grab UNSAFE");
               }

               Field fieldImplLookup = Lookup.class.getDeclaredField("IMPL_LOOKUP");
               Lookup LOOKUP = (Lookup)UNSAFE.getObject(UNSAFE.staticFieldBase(fieldImplLookup), UNSAFE.staticFieldOffset(fieldImplLookup));
               UnionFileSystemProvider UFSP = (UnionFileSystemProvider)LOOKUP.findStaticVarHandle(JarContentsImpl.class, "UFSP", UnionFileSystemProvider.class)
                  .get();
               Map<String, UnionFileSystem> fileSystems = (Map)LOOKUP.findVarHandle(UnionFileSystemProvider.class, "fileSystems", Map.class)
                  .get((UnionFileSystemProvider)UFSP);
               copyFileSystems.putAll(fileSystems);
               VarHandle fsVH = LOOKUP.findVarHandle(Jar.class, "filesystem", UnionFileSystem.class);
               ModList.get()
                  .getModFiles()
                  .stream()
                  .map(modFile -> (UnionFileSystem)fsVH.get((SecureJar)modFile.getFile().getSecureJar()))
                  .forEach(validFS::add);
            } catch (Throwable e) {
               AllTheLeaks.LOGGER.error("Failed to clear dangling mod jars", e);
            }

            Pattern MATCH_PATTERN = Pattern.compile("(.*)#(\\d+)");
            Map<String, Pair<Integer, UnionFileSystem>> tempMap = new HashMap<>();

            for (Entry<String, UnionFileSystem> entry : copyFileSystems.entrySet()) {
               String key = entry.getKey();
               if (StringUtils.countMatches(key, '#') <= 1) {
                  UnionFileSystem value = entry.getValue();
                  Matcher match = MATCH_PATTERN.matcher(key);
                  if (match.find()) {
                     String newkey = match.group(1);
                     Integer newIndex = Integer.valueOf(match.group(2));
                     Pair<Integer, UnionFileSystem> oldVal = tempMap.put(newkey, Pair.of(newIndex, value));
                     if (oldVal != null) {
                        if ((Integer)oldVal.getFirst() < newIndex) {
                           try {
                              if (validFS.contains(oldVal.getSecond())) {
                                 AllTheLeaks.LOGGER.warn("Tried to release a valid jar {}#{}, skipping...", newkey, oldVal.getFirst());
                              } else {
                                 AllTheLeaks.LOGGER.info("Releasing leaked jar: {}#{}", newkey, oldVal.getFirst());
                                 ((UnionFileSystem)oldVal.getSecond()).close();
                              }
                           } catch (IOException e) {
                              AllTheLeaks.LOGGER.error("Failed to close leaked jar.", e);
                           }
                        } else {
                           try {
                              if (validFS.contains(value)) {
                                 AllTheLeaks.LOGGER.warn("Tried to release a valid jar {}#{}, skipping...", newkey, newIndex);
                              } else {
                                 AllTheLeaks.LOGGER.info("Releasing leaked jar: {}#{}", newkey, newIndex);
                                 value.close();
                              }
                           } catch (IOException e) {
                              AllTheLeaks.LOGGER.error("Failed to close leaked jar.", e);
                           }
                        }
                     }
                  }
               }
            }
         }
      );
   }
}
