package net.snackbag.tabmanager.filesystem;

import net.fabricmc.loader.api.FabricLoader;
import net.snackbag.tabmanager.TabManagerClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigDirectory {

    private static final String MOD_CONFIG_FILE_BAK = "tabmanager_config_bak.json";
    private static final String MOD_CONFIG_FILE = "tabmanager_config.json";
    private static final String MOD_CONFIG_DIR = "tabmanager";

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_CONFIG_DIR);
    }

    public static File getConfigFile() {
        return getConfigDirectory().resolve(MOD_CONFIG_FILE).toFile();
    }

    public static File getBakConfigFile() {
        return getConfigDirectory().resolve(MOD_CONFIG_FILE_BAK).toFile();
    }

    /**
     * Ensures the config directory exists
     */
    public static void ensureConfigDirectoryExists() {
        Path configDir = getConfigDirectory();
        if (!configDir.toFile().exists()) {
            configDir.toFile().mkdirs();
        }
    }

    /**
     * Ensures the config file exists.
     * @return true if successful, otherwise false
     */
    public static boolean ensureConfigFileExists() {
        Path configFile = getConfigDirectory().resolve(MOD_CONFIG_FILE);
        if (!configFile.toFile().exists()) {
            try {
                if (configFile.toFile().createNewFile())
                    TabManagerClient.LOGGER.info("Created new config file at {}", configFile.toAbsolutePath());
                else
                    TabManagerClient.LOGGER.error("File already exists at {}", configFile.toAbsolutePath());
            } catch (Exception e) {
                TabManagerClient.LOGGER.error("Failed to create config file: {}", e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * Ensures the backup config file exists.
     * @return true if successful, otherwise false
     */
    public static boolean ensureBakConfigFileExists() {
        File bakConfigFile = getBakConfigFile();
        if (bakConfigFile.exists()) {
            try {
                if (bakConfigFile.createNewFile())
                    TabManagerClient.LOGGER.info("Created new bak config file at {}", bakConfigFile.toPath());
                else
                    TabManagerClient.LOGGER.error("File already exists at {}", bakConfigFile.toPath());
            } catch (IOException e) {
                TabManagerClient.LOGGER.error("Failed to create bak config file: {}", e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a backup of current config file.
     * Used for if user loads a custom config
     */
    public static void backupConfigFile() throws IOException {
        ensureConfigDirectoryExists();
        ensureConfigFileExists();
        ensureBakConfigFileExists();

        try (
                FileInputStream fileInputStream = new FileInputStream(getConfigFile());
                FileOutputStream fileOutputStream = new FileOutputStream(getBakConfigFile());
        ) {
            byte[] fileContent = fileInputStream.readAllBytes();
            if (fileContent.length == 0) return; // Do nothing if config is empty
            fileOutputStream.write(fileContent);
        }
    }
}
