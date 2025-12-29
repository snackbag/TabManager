package net.snackbag.tabmanager.access;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.util.ItemFilter;

public interface ItemGroupAccessor {

    /**
     * Gets the unique Identifier key for this tab.
     * @return The tab's unique Identifier.
     */
    Identifier tabmanager$getTabKey();

    /**
     * Sets the unique Identifier key for this tab.
     * @param id The tab's unique Identifier.
     */
    void tabmanager$setTabKey(Identifier id);

    /**
     * Checks if the tab is hidden.
     * @return True if the tab is hidden, false otherwise.
     */
    boolean tabmanager$isHidden();

    /**
     * Sets the hidden status of the tab.
     * @param hidden True to hide the tab, false to show it.
     */
    void tabmanager$setHidden(boolean hidden);

    /**
     * Sets the column index for the tab.
     * This is the position of where the tab is rendered in the creative menu.
     * @param column The column index.
     */
    void tabmanager$setColumn(int column);

    /**
     * Sets the row index for the tab.
     * This is the position of where the tab is rendered in the creative menu.
     * @param row Either ItemGroup.Row.TOP or ItemGroup.Row.BOTTOM. See {@link ItemGroup.Row}.
     */
    void tabmanager$setRow(ItemGroup.Row row);

    /**
     * Sets the icon of the ItemGroup
     * @param istack The {@link ItemStack} for the icon
     */
    void tabmanager$setIcon(ItemStack istack);

    /**
     * Applies the item filter to determine which items are displayed in this tab.
     */
    void tabmanager$applyFilterDisplayItems(ItemFilter filter);

    /**
     * Resets the displayed items to the default state (all items).
     */
    void tabmanager$resetDisplayItems();

    /**
     * Gets the page number of the tab.
     * @return The page number.
     */
    int tabmanager$getPage();

    /**
     * Sets the page number of the tab.
     * @param page The page number.
     */
    void tabmanager$setPage(int page);

    /**
     * Determines if tab should be displayed the vanilla-way.
     */
    boolean shouldDisplayVanilla();

    /**
     * Gets the type of the ItemGroup.
     */
    ItemGroup.Type tabmanager$getType();

}
