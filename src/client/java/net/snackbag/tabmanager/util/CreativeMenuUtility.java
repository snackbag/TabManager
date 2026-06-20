package net.snackbag.tabmanager.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.mixin_interface.FabricCreativeGuiComponentsInterface;

import java.util.Comparator;
import java.util.List;

public class CreativeMenuUtility {

    /**
     * @return Count of pages on Creative Menu Tab
     */
    public static int getPageCount() {
        return FabricCreativeGuiComponentsInterface.tabmanager$getInventoryPageCount();
    }

    // Copied from CreativeInventoryScreenMixin on Fabric's side and slightly modified

    /**
     * Gets the item groups on a specific page. Filters out special groups.
     * @param page The page to get the item groups for
     * @param allGroups Whether to include all groups or only non-special groups
     * @return The item groups on the specified page
     */
    public static List<ItemGroup> getItemGroupsOnPage(int page, boolean allGroups) {
        List<ItemGroup> groupsOnPage = allGroups ? ItemGroups.getGroups() : ItemGroups.getGroupsToDisplay();
        return groupsOnPage
                .stream()
                .filter(itemGroup -> ((ItemGroupAccessor) itemGroup).tabmanager$getPage() == page)
                .filter(itemGroup -> !itemGroup.isSpecial())
                // Thanks to isXander for the sorting
                .sorted(Comparator.comparing(ItemGroup::getRow).thenComparingInt(ItemGroup::getColumn))
                .sorted((a, b) -> {
                    if (a.isSpecial() && !b.isSpecial()) return 1;
                    if (!a.isSpecial() && b.isSpecial()) return -1;
                    return 0;
                })
                .toList();
    }
}
