package net.snackbag.tabmanager.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.access.AdditionalTabInfoAccessor;

public class TabCommand {

    /**
     * Registers the Tab Command
     * @param dispatcher The Command Dispatcher
     */
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("tabmd") // "tabmd" = "Tab Manager Debug"
                        .then(ClientCommandManager.literal("hide")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(src -> changeTabVisibility(src, true))))
                        .then(ClientCommandManager.literal("show")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(src -> changeTabVisibility(src, false))))
                        .then(ClientCommandManager.literal("changeCol")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("column", IntegerArgumentType.integer()).executes(TabCommand::changeTabColumn))))
                        .then(ClientCommandManager.literal("changeRow")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("row", IntegerArgumentType.integer()).executes(TabCommand::changeTabRow))))
                        .then(ClientCommandManager.literal("printGroupPairs").executes(TabCommand::printGroupPairs))
        );
    }

    /**
     * Changes the visibility status of the given tab.
     * @param cmdSource The commands source containing the tab id.
     * @param hide The target visibility status.
     * @return Always 1
     */
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

    /**
     * Changes the column of the given tab.
     * @param cmdSource The command source containing the tab id and the target column index.
     * @return Always 1
     */
    private static int changeTabColumn(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");
        int targetColumn =  IntegerArgumentType.getInteger(cmdSource, "column");

        ItemGroup targetGroup = ItemGroups.getGroups().stream().filter(igroup -> ((AdditionalTabInfoAccessor) igroup).tabmanager$getTabKey().toString().equals(tabId)).findFirst().orElse(null);

        if (targetGroup == null) {
            player.sendMessage(Text.literal("No group with id '" + tabId + "' was found."), false);
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(Text.literal("Setting column for ItemGroup '" + tabId + "': " + targetGroup.getColumn() + " -> " + targetColumn)); // --> "Setting column for ItemGroup 'minecraft:something': 3 -> 4
        ((AdditionalTabInfoAccessor) targetGroup).tabmanager$setColumn(targetColumn);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Changes the row of the given tab.
     * @param cmdSource The command source containing the tab id and the target row index. 0 = BOTTOM; 1 = TOP
     * @return Always 1
     * @see ItemGroup.Row
     */
    private static int changeTabRow(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");
        int row = IntegerArgumentType.getInteger(cmdSource, "row");

        if (row < 0 || row > 1) {
            player.sendMessage(Text.literal("Invalid row number given. Use 0 for bottom or 1 for top."), false);
            return Command.SINGLE_SUCCESS;
        }

        ItemGroup targetGroup = ItemGroups.getGroups().stream().filter(igroup -> ((AdditionalTabInfoAccessor) igroup).tabmanager$getTabKey().toString().equals(tabId)).findFirst().orElse(null);

        if (targetGroup == null) {
            player.sendMessage(Text.literal("No group with id '" + tabId + "' was found."), false);
            return Command.SINGLE_SUCCESS;
        }

        ItemGroup.Row currentRow = targetGroup.getRow();
        ItemGroup.Row targetRow = row == 0 ? ItemGroup.Row.BOTTOM : ItemGroup.Row.TOP;

        player.sendMessage(Text.literal("Setting row for ItemGroup '" + tabId + "': " + currentRow + " -> " + targetRow)); // --> "Setting row for ItemGroup 'minecraft:something': TOP -> BOTTOM

        ((AdditionalTabInfoAccessor) targetGroup).tabmanager$setRow(targetRow);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Prints out the available ItemGroups along with their IDs.
     * @param cmdSource The command source containing the player.
     * @return Always 1
     */
    private static int printGroupPairs(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();

        ItemGroups.getGroups().forEach(igroup ->
                player.sendMessage(Text.literal("ItemGroup: " + igroup.getDisplayName().getString() + " | ID: " + ((AdditionalTabInfoAccessor)igroup).tabmanager$getTabKey() + " | Row: " + igroup.getRow() + " | Column: " + igroup.getColumn()), false)
        );

        return Command.SINGLE_SUCCESS;
    }
}
