package net.snackbag.tabmanager.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.TabManagerClient;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.util.ItemFilter;
import net.snackbag.tabmanager.util.RegexCompiler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class TabCommand {

    /**
     * Registers the Tab Command
     * @param dispatcher The Command Dispatcher
     * @param registryAccess The Command Registry Access
     */
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
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
                        .then(ClientCommandManager.literal("changeIcon")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(TabCommand::changeTabIcon))))
                        .then(ClientCommandManager.literal("printGroupPairs").executes(TabCommand::printGroupPairs))
                        .then(ClientCommandManager.literal("printDisplayStacks")
                                .then(ClientCommandManager.argument("id",  StringArgumentType.string()).executes(TabCommand::printDisplayStack)))
                        .then(ClientCommandManager.literal("changePage")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("page", IntegerArgumentType.integer()).executes(TabCommand::changePage))))
                        .then(ClientCommandManager.literal("addPage").executes(TabCommand::addPage))
                        .then(ClientCommandManager.literal("removePage").executes(TabCommand::removePage))
                        .then(ClientCommandManager.literal("applyFilter")
                                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                                        .then(ClientCommandManager.literal("regex")
                                                .then(ClientCommandManager.argument("filter", StringArgumentType.string()).executes(src -> applyFilter(src, true))))
                                        .then(ClientCommandManager.literal("glob")
                                                .then(ClientCommandManager.argument("filter", StringArgumentType.string()).executes(src ->  applyFilter(src, false))))))
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

        ItemGroups.getGroups().stream().filter(group -> ((ItemGroupAccessor)group).tabmanager$getTabKey().toString().equals(tabId)).forEach(group -> {
            player.sendMessage(Text.literal("Found group: " + ((ItemGroupAccessor)group).tabmanager$getTabKey().toString()), false);
            ((ItemGroupAccessor)group).tabmanager$setHidden(hide);
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
        int targetColumn = IntegerArgumentType.getInteger(cmdSource, "column");

        ItemGroup targetGroup = getItemGroupOrError(tabId, player);
        if (targetGroup == null) return Command.SINGLE_SUCCESS;

        player.sendMessage(Text.literal("Setting column for ItemGroup '" + tabId + "': " + targetGroup.getColumn() + " -> " + targetColumn)); // --> "Setting column for ItemGroup 'minecraft:something': 3 -> 4
        ((ItemGroupAccessor) targetGroup).tabmanager$setColumn(targetColumn);

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

        ItemGroup targetGroup = getItemGroupOrError(tabId, player);
        if (targetGroup == null) return Command.SINGLE_SUCCESS;

        ItemGroup.Row currentRow = targetGroup.getRow();
        ItemGroup.Row targetRow = row == 0 ? ItemGroup.Row.BOTTOM : ItemGroup.Row.TOP;

        player.sendMessage(Text.literal("Setting row for ItemGroup '" + tabId + "': " + currentRow + " -> " + targetRow)); // --> "Setting row for ItemGroup 'minecraft:something': TOP -> BOTTOM

        ((ItemGroupAccessor) targetGroup).tabmanager$setRow(targetRow);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Changes the icon of a tab in the creative menu
     * @param cmdSource The command source containing the target tab id and the target item icon
     * @return Always 1
     */
    private static int changeTabIcon(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");
        ItemStackArgument stack = ItemStackArgumentType.getItemStackArgument(cmdSource, "item");

        ItemGroup targetGroup = getItemGroupOrError(tabId, player);
        if (targetGroup == null) return Command.SINGLE_SUCCESS;

        ItemStack currentIcon = targetGroup.getIcon();
        ItemStack targetIcon;

        try {
            targetIcon = stack.createStack(1, false);
        } catch (CommandSyntaxException e) {
            TabManagerClient.LOGGER.error("Error while creating tab icon for ItemGroup '{}'", tabId, e);
            player.sendMessage(Text.literal("Error while creating tab icon for ItemGroup '" + tabId + "'. Please check logs."), false);
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(Text.literal("Changing Icon for ItemGroup '" + tabId + "': " + currentIcon.getItem().toString() + " -> " + targetIcon.getItem().toString()), false); // --> "Changing Icon for ItemGroup 'minecraft:something': minecraft:iron_ingot -> minecraft:gold_ingot

        ((ItemGroupAccessor) targetGroup).tabmanager$setIcon(targetIcon);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Changes the page of a tab in the creative menu
     * @param cmdSource The cmdSource containing the target page and item group
     * @return Always 1
     */
    private static int changePage(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");
        int targetPage = IntegerArgumentType.getInteger(cmdSource, "page");
        int maxPage = FabricCreativeGuiComponents.getPageCount() - 1;

        if (targetPage < 0 || targetPage > maxPage) {
            player.sendMessage(Text.literal("Target page must be between 0 and " + maxPage), false);
            return Command.SINGLE_SUCCESS;
        }

        ItemGroup targetGroup = getItemGroupOrError(tabId, player);
        if (targetGroup == null) return Command.SINGLE_SUCCESS;

        int currentPage = ((FabricItemGroupImpl) targetGroup).fabric_getPage();

        player.sendMessage(Text.literal("Changing Page for ItemGroup '" + tabId + "': " + currentPage + " -> " + targetPage), false);

        ((FabricItemGroupImpl) targetGroup).fabric_setPage(targetPage);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Prints out the available ItemGroups along with their IDs.
     * @param cmdSource The command source containing the player.
     * @return Always 1
     */
    // Warnings here are fine as far as I (Tobias) know. I wasn't able to find an according value for "@SuppressWarning()" to suppress it unfortunately.
    private static int printGroupPairs(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();

        ItemGroups.getGroups().forEach(igroup ->
                player.sendMessage(Text.literal("ItemGroup: " + igroup.getDisplayName().getString() + " | ID: " +
                        ((ItemGroupAccessor)igroup).tabmanager$getTabKey() + " | Row: " + igroup.getRow() + " | Column: "
                        + igroup.getColumn() + " | Page: " + ((FabricItemGroupImpl)igroup).fabric_getPage()), false)
        );

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Prints out the displayed items in an item group.
     * @param cmdSource The Command Source containing the target item group.
     * @return Always 1
     */
    private static int printDisplayStack(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");

        ItemGroup targetGroup = getItemGroupOrError(tabId, player);
        if (targetGroup == null) return Command.SINGLE_SUCCESS;

        targetGroup.getDisplayStacks().forEach(istack -> {
            player.sendMessage(Text.literal("ItemStack: " + istack.toString() + " | Item: " + istack.getItem().toString()), false);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int applyFilter(CommandContext<FabricClientCommandSource> cmdSource, boolean isRegex) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        String tabId = StringArgumentType.getString(cmdSource, "id");
        String predicateSource = (isRegex ? "regex:" : "glob:") + StringArgumentType.getString(cmdSource, "filter");

        ItemGroup targetGroup = getItemGroupOrError(tabId, player);
        if (targetGroup == null) return Command.SINGLE_SUCCESS;

        // Compile the pattern from the raw filter (without the prefix)
        ItemFilter itemFilter = ItemFilter.parse(predicateSource);
        itemFilter.addApplicableGroup(targetGroup);
        Config.INSTANCE.filters.add(itemFilter);
        Config.reload();

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Adds a page to the creative menu.
     * @param cmdSource The Command Source containing mainly the player
     * @return Always 1
     */
    private static int addPage(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();

        Config.INSTANCE.fakePages++;

        player.sendMessage(Text.literal("Creative Menu now has " + FabricCreativeGuiComponents.getPageCount() + " pages!"));

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Removes the highest page from the creative menu
     * @param cmdSource The Command Source containing mainly the player
     * @return Always 1
     */
    private static int removePage(CommandContext<FabricClientCommandSource> cmdSource) {
        PlayerEntity player = cmdSource.getSource().getPlayer();
        int highestPage = FabricCreativeGuiComponents.getPageCount() - 1;

        if (Config.INSTANCE.fakePages <= 0) {
            player.sendMessage(Text.literal("Cannot remove pages; cannot go lower than " + FabricCreativeGuiComponents.getPageCount() + " pages!"), false);
            return Command.SINGLE_SUCCESS;
        }

        // Move objects on page to free spots on pages below
        List<ItemGroup> groupsToMove = ItemGroups.getGroups()
                .stream()
                .filter(o -> ((ItemGroupAccessor) o).tabmanager$getPage() == highestPage)
                .toList();

        for (ItemGroup itemGroup : groupsToMove) {
            boolean spotFound = false;
            for (int page = highestPage - 1; page >= 0; page--) { // Scan all pages from highest to lowest for free spots
                for (int row = 0; row <= 1; row++) { // Scan both rows for free spots
                    for (int col = 0; col <= 4; col++) { // Scan all columns on that row for free spots; one row has 5 columns (0-4)
                        final int finalPage = page;
                        final int finalRow = row;
                        final int finalCol = col;

                        boolean spotTaken = ItemGroups.getGroups().stream().anyMatch(igroup ->
                                ((ItemGroupAccessor) igroup).tabmanager$getPage() == finalPage &&
                                        igroup.getRow().ordinal() == finalRow &&
                                        igroup.getColumn() == finalCol
                        );

                        if (!spotTaken) {
                            // Spot is free, move the item group here
                            player.sendMessage(Text.literal("Moving ItemGroup '" + ((ItemGroupAccessor) itemGroup).tabmanager$getTabKey().toString() +
                                    "' from Page " + highestPage + " to Page " + finalPage + ", Row " + finalRow + ", Column " + finalCol), false);
                            ((ItemGroupAccessor) itemGroup).tabmanager$setPage(finalPage);
                            ((ItemGroupAccessor) itemGroup).tabmanager$setRow(row == 1 ? ItemGroup.Row.BOTTOM : ItemGroup.Row.TOP); // Set row
                            ((ItemGroupAccessor) itemGroup).tabmanager$setColumn(finalCol); // Set column
                            spotFound = true;
                            break; // Break out of column loop
                        }
                    }

                    if (spotFound) break; // Break out of row loop
                }

                if (spotFound) break; // Break out of page loop
            }
        }

        Config.INSTANCE.fakePages--; // Finally, remove the page

        return Command.SINGLE_SUCCESS;
    }


    // UTILITY METHODS --------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------

    /**
     * Returns the ItemGroup or errors out and prints an error message in the player's chat.
     * @return The ItemGroup, or null if non found.
     */
    private static @Nullable ItemGroup getItemGroupOrError(String id, PlayerEntity player) {
        ItemGroup targetGroup = ItemGroups.getGroups().stream().filter(igroup -> ((ItemGroupAccessor) igroup).tabmanager$getTabKey().toString().equals(id)).findFirst().orElse(null);

        if (targetGroup == null) {
            player.sendMessage(Text.literal("No group with id '" + id + "' was found."), false);
            return null;
        }

        return targetGroup;
    }
}
