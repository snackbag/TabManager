package net.snackbag.tabmanager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.snackbag.tabmanager.command.ModCommands;
import net.snackbag.tabmanager.config.ItemGroupSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TabManagerClient implements ClientModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ItemGroupSettings.populateItemGroups();

        ClientCommandRegistrationCallback.EVENT.register(ModCommands::registerCommands);
	}

    /*
    TODO:
       - Item Masks
       - GUI feature for reordering (maybe)
     */
}