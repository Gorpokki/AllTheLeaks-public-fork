package dev.uncandango.alltheleaks;

import java.nio.file.Path;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("alltheleaks")
public class AllTheLeaks {
   public static final String MOD_ID = "alltheleaks";
   public static final Logger LOGGER = LoggerFactory.getLogger("AllTheLeaks");
   public static final boolean INDEV = Boolean.getBoolean("alltheleaks.indev");
   private static final Path LOCAL = FMLPaths.GAMEDIR.get().resolve("local").resolve("alltheleaks");

   public AllTheLeaks(IEventBus eventModBus, ModContainer modContainer) {
   }

   public static Path getLocal() {
      return LOCAL;
   }
}
