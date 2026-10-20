package dev.uncandango.alltheleaks.feature.common.mods.minecraft;

import dev.uncandango.alltheleaks.AllTheLeaks;
import dev.uncandango.alltheleaks.annotation.Issue;
import dev.uncandango.alltheleaks.utils.ReflectionHelper;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.FileNameDateFormatter;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import org.jetbrains.annotations.NotNull;

@Issue(modId = "minecraft", issueId = "Backup save with loaded chunks only", versionRange = "1.21.1")
public class SaveWithLoadedChunks {
   private static final MethodHandle GET_CHUNKS = ReflectionHelper.getMethodFromClass(ChunkMap.class, "getChunks", MethodType.methodType(Iterable.class), false);
   private static final MethodHandle CHECK_LOCK = ReflectionHelper.getMethodFromClass(
      LevelStorageAccess.class, "checkLock", MethodType.methodType(void.class), false
   );
   private static final VarHandle STORAGE_SOURCE = ReflectionHelper.getFieldFromClass(MinecraftServer.class, "storageSource", LevelStorageAccess.class, false);
   private static final DateTimeFormatter FORMATTER = FileNameDateFormatter.create();
   private static final UUID CHUNK_OWNER = UUID.fromString("fe2efb68-0207-4255-b2dd-02d197309304");
   public static final TicketController ATL_CHUNKLOADER = new TicketController(
      ResourceLocation.fromNamespaceAndPath("alltheleaks", "chunkloader"), (level, helper) -> helper.removeAllTickets(CHUNK_OWNER)
   );

   public static int loadChunksFromSaveFile(CommandSourceStack source) {
      MinecraftServer server = source.getServer();
      LevelStorageAccess storage = (LevelStorageAccess)STORAGE_SOURCE.get((MinecraftServer)server);
      Path worldDir = storage.getWorldDir();
      String worldName = storage.getLevelId();
      Path chunksFile = worldDir.resolve(worldName).resolve("alltheleaks_chunks.dat");
      AtomicInteger count = new AtomicInteger();
      if (Files.exists(chunksFile)) {
         try {
            CompoundTag loadedChunksData = NbtIo.readCompressed(chunksFile, NbtAccounter.unlimitedHeap());

            for (String dimension : loadedChunksData.getAllKeys()) {
               ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimension)));
               if (level == null) {
                  AllTheLeaks.LOGGER.warn("Level {} was not found while trying to load chunks", dimension);
               } else if (loadedChunksData.get(dimension) instanceof ListTag lt) {
                  for (int i = 30; i <= 33; i++) {
                     for (long chunk : lt.getLongArray(i)) {
                        ChunkPos chunkPos = new ChunkPos(chunk);
                        if (i <= 31) {
                           ATL_CHUNKLOADER.forceChunk(level, CHUNK_OWNER, chunkPos.x, chunkPos.z, true, true);
                           count.incrementAndGet();
                           AllTheLeaks.LOGGER.debug("Chunkloaded {} from dimension {} is with ticket {}", new Object[]{chunkPos, dimension, i});
                        }

                        if (i == 33) {
                           level.getBlockState(chunkPos.getMiddleBlockPosition(60));
                        }
                     }
                  }
               }
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }

         source.sendSuccess(
            () -> Component.translatable("Forceloaded %s chunks from backup. It will be unloaded when world is loaded again.", new Object[]{count.get()}),
            false
         );
         return 1;
      } else {
         source.sendFailure(Component.literal("No AllTheLeaks file with chunk info was found on this save."));
         return 0;
      }
   }

   public static int saveWorld(CommandSourceStack source) {
      MinecraftServer server = source.getServer();
      LevelStorageAccess storage = (LevelStorageAccess)STORAGE_SOURCE.get((MinecraftServer)server);
      Path worldDir = storage.getWorldDir();
      String worldName = storage.getLevelId();
      String fileName = LocalDateTime.now().format(FORMATTER) + "_" + worldName;

      try {
         FileUtil.createDirectoriesSafe(AllTheLeaks.getLocal());
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      try {
         CHECK_LOCK.invoke((LevelStorageAccess)storage);
      } catch (Throwable e) {
         AllTheLeaks.LOGGER.error("Error while invoking checkLock", e);
         return 0;
      }

      server.executeBlocking(() -> server.saveEverything(true, true, true));
      CompoundTag ticketPerLevel = new CompoundTag();
      final Map<String, Set<String>> regionsToSave = new HashMap<>();

      for (ServerLevel level : server.getAllLevels()) {
         try {
            for (ChunkHolder chunkholder : (Iterable)GET_CHUNKS.invoke((ChunkMap)level.getChunkSource().chunkMap)) {
               ChunkAccess chunk = chunkholder.getLatestChunk();
               if (chunk != null) {
                  String dimension = level.dimension().location().toString();
                  ChunkPos chunkPos = chunk.getPos();
                  if (!ticketPerLevel.contains(dimension)) {
                     ListTag taglist = new ListTag(47);

                     for (int i = 0; i < 47; i++) {
                        taglist.add(i, new LongArrayTag(new long[0]));
                     }

                     ticketPerLevel.put(dimension, taglist);
                  }

                  ((LongArrayTag)ticketPerLevel.getList(dimension, 12).get(chunkholder.getTicketLevel())).add(LongTag.valueOf(chunkPos.toLong()));
                  regionsToSave.computeIfAbsent(dimension, key -> new HashSet<>())
                     .add(String.format(Locale.ROOT, "r.%d.%d", chunkPos.getRegionX(), chunkPos.getRegionZ()));
               }
            }
         } catch (Throwable e) {
            AllTheLeaks.LOGGER.error("Error while invoking getChunks", e);
            return 0;
         }
      }

      Path outputFile = getOutputFile(fileName);

      try (final ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(outputFile), 65536))) {
         final Path relative = Paths.get(worldName);
         final Path savePath = worldDir.resolve(relative).toRealPath();
         String s1 = relative.resolve("alltheleaks_chunks.dat").toString().replace('\\', '/');
         ZipEntry zipentry = new ZipEntry(s1);
         zipStream.putNextEntry(zipentry);
         ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
         NbtIo.writeCompressed(ticketPerLevel, byteStream);
         byteStream.writeTo(zipStream);
         zipStream.closeEntry();
         Files.walkFileTree(savePath, new SimpleFileVisitor<Path>() {
            private static final Set<String> validFolders = Set.of("entities", "poi", "region");

            @NotNull
            public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
               if (file.endsWith("session.lock")) {
                  return FileVisitResult.CONTINUE;
               }

               if (file.getFileName().toString().startsWith("r.")) {
                  String dimension = null;
                  Path parent1 = file.getParent();
                  if (validFolders.contains(parent1.getFileName().toString())) {
                     Path parent2 = parent1.getParent();
                     if (parent2.equals(savePath)) {
                        dimension = "minecraft:overworld";
                     } else {
                        String p2 = parent2.getFileName().toString();
                        if (p2.equals("DIM1")) {
                           dimension = "minecraft:the_end";
                        } else if (p2.equals("DIM-1")) {
                           dimension = "minecraft:the_nether";
                        }

                        if (dimension == null) {
                           Path p3 = parent2.getParent();
                           dimension = p3.getFileName().toString() + ":" + p2;
                        }
                     }

                     String fullRegionFileName = file.getFileName().toString();
                     String regionFileName = fullRegionFileName.substring(0, fullRegionFileName.lastIndexOf(46));
                     Set<String> validRegions = regionsToSave.get(dimension);
                     if (validRegions == null || !validRegions.contains(regionFileName)) {
                        return FileVisitResult.CONTINUE;
                     }
                  }
               }

               String s1x = relative.resolve(savePath.relativize(file)).toString().replace('\\', '/');
               ZipEntry zipentryx = new ZipEntry(s1x);
               zipStream.putNextEntry(zipentryx);
               com.google.common.io.Files.asByteSource(file.toFile()).copyTo(zipStream);
               zipStream.closeEntry();
               return FileVisitResult.CONTINUE;
            }
         });
      } catch (IOException e) {
         AllTheLeaks.LOGGER.error("Error while trying to save world", e);
         throw new RuntimeException(e);
      }

      source.sendSuccess(() -> Component.literal("Backup with only loaded chunks saved at ./local/alltheleaks/"), true);
      return 1;
   }

   private static Path getOutputFile(String name) {
      try {
         Files.createDirectories(AllTheLeaks.getLocal());
         return AllTheLeaks.getLocal().resolve(FileUtil.findAvailableName(AllTheLeaks.getLocal(), name, ".zip"));
      } catch (IOException e) {
         AllTheLeaks.LOGGER.error("Error while creating folder", e);
         throw new RuntimeException(e);
      }
   }
}
