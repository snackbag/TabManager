package net.snackbag.tabmanager.util;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.snackbag.tabmanager.access.ItemGroupAccessor;

import java.util.Comparator;
import java.util.List;

public class CreativeMenuUtility {

    @SuppressWarnings("UnstableApiUsage")
    public static int getPageCount() {
        return FabricCreativeGuiComponents.getPageCount();
    }

    // Copied from CreativeInventoryScreenMixin on Fabric's side and slightly modified
    public static List<ItemGroup> getItemGroupsOnPage(int page) {
        return ItemGroups.getGroupsToDisplay()
                .stream()
                .filter(itemGroup -> ((ItemGroupAccessor) itemGroup).tabmanager$getPage() == page)
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
