package net.snackbag.tabmanager.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.access.AdditionalTabInfoAccessor;

public class TabCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("tabmd") // "tabmd" * "Tab Manager Debug"
                        .then(ClientCommandManager.literal("hide")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(src -> changeTabVisibility(src, true))))
                        .then(ClientCommandManager.literal("show")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(src -> modifyTab(src, false))))
                                .then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(src -> changeTabVisibility(src, false))))
                        .then(ClientCommandManager.literal("printGroupPairs").executes(TabCommand::printGroupPairs))
        );
    }

    private static int changeTabVisibility(CommandContext<FabricClientCommandSource> cmdSource, boolean hide) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");

        player.sendMessage(Text.literal("Hiding tab for display name: "), false);

        ItemGroups.getGroups().stream().filter(group -> ((AdditionalTabInfoAccessor)group).tabmanager$getTabKey().toString().equals(tabId)).forEach(group -> {
            player.sendMessage(Text.literal("Found group: " + ((AdditionalTabInfoAccessor)group).tabmanager$getTabKey().toString()), false);
            ((AdditionalTabInfoAccessor)group).tabmanager$setHidden(hide);
            player.sendMessage(Text.literal("Tab hidden."), false);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int printGroupPairs(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();

        ItemGroups.getGroups().forEach(igroup ->
                player.sendMessage(Text.literal("ItemGroup: " + igroup.getDisplayName().getString() + " | ID: " + ((AdditionalTabInfoAccessor)igroup).tabmanager$getTabKey()), false)
        );

        return Command.SINGLE_SUCCESS;
    }
}
