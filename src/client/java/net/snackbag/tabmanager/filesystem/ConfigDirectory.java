package net.snackbag.tabmanager.filesystem;

import net.fabricmc.loader.api.FabricLoader;
import net.snackbag.tabmanager.TabManagerClient;

import java.io.File;
import java.nio.file.Path;

public class ConfigDirectory {

    private static final String MOD_CONFIG_FILE = "tabmanager_config.json";
    private static final String MOD_CONFIG_DIR = "tabmanager";

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_CONFIG_DIR);
    }

    public static File getConfigFile() {
        return getConfigDirectory().resolve(MOD_CONFIG_FILE).toFile();
    }

    public static void ensureConfigDirectoryExists() {
        Path configDir = getConfigDirectory();
        if (!configDir.toFile().exists()) {
            configDir.toFile().mkdirs();
        }
    }

    public static void ensureConfigFileExists() {
        Path configFile = getConfigDirectory().resolve(MOD_CONFIG_FILE);
        if (!configFile.toFile().exists()) {
            try {
                if (configFile.toFile().createNewFile())
                    TabManagerClient.LOGGER.info("Created new config file at {}", configFile.toAbsolutePath());
                else
                    TabManagerClient.LOGGER.error("File already exists at {}", configFile.toAbsolutePath());
            } catch (Exception e) {
                TabManagerClient.LOGGER.error("Failed to create config file: {}", e.getMessage());
            }
        }
    }

}
