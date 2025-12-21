package net.snackbag.tabmanager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.snackbag.tabmanager.command.ModCommands;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.filesystem.ConfigDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static net.snackbag.tabmanager.util.ItemGroupUtility.applyFilters;
import static net.snackbag.tabmanager.util.ItemGroupUtility.populateItemGroups;

public class TabManagerClient implements ClientModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

        initialiseItemGroups();
        loadConfig();

        ClientCommandRegistrationCallback.EVENT.register(ModCommands::registerCommands);
	}

    private void initialiseItemGroups() {
        populateItemGroups();
        applyFilters();
    }

    private void loadConfig() {
        ConfigDirectory.ensureConfigDirectoryExists()

        if (!ConfigDirectory.ensureConfigFileExists())
            TabManagerClient.LOGGER.error("Failed to create config file! All changed will be for this session only!");

        try {
            Config.loadConfigFile(ConfigDirectory.getConfigFile());
        } catch (IOException e) {
            TabManagerClient.LOGGER.error("Failed to load config file!", e);
        }
    }

    /*
    TODO:
       - GUI feature for reordering (maybe)
     */
}