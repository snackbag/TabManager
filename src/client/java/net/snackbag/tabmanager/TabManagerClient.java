package net.snackbag.tabmanager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.snackbag.tabmanager.command.ModCommands;
import net.snackbag.tabmanager.config.ItemGroupSettings;

public class TabManagerClient implements ClientModInitializer {
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
       - Change Icon of Tab
     */
}