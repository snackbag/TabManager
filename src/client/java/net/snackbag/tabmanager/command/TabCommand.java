package net.snackbag.tabmanager.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.config.ItemGroupSettings;

public class TabCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("tabmd") // "tabmd" * "Tab Manager Debug"
                        .then(ClientCommandManager.literal("hide")
                                .then(ClientCommandManager.argument("displayName", StringArgumentType.string()).executes(TabCommand::hideTab)))
                        .then(ClientCommandManager.literal("printGroupPairs").executes(TabCommand::printGroupPairs))
        );
    }

    private static int hideTab(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String displayName = StringArgumentType.getString(cmdSource, "displayName");

        player.sendMessage(Text.literal("Hiding tab for display name: "), false);

//        ItemGroups.getGroups().stream().filter(group -> group.getDisplayName().getString().equals(displayName)).forEach(group -> {
//            player.sendMessage(Text.literal("Found group: " + group.getDisplayName().getString()), false);
//            group.shouldDisplay()
//        });

        return Command.SINGLE_SUCCESS;
    }

    private static int printGroupPairs(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();

        ItemGroupSettings.ITEM_GROUPS.forEach(pair -> {
            player.sendMessage(Text.literal("ItemGroup: " + pair.itemGroup().getDisplayName().getString() + " | ID: " + pair.id().toString()), false);
        });

        return Command.SINGLE_SUCCESS;
    }
}
