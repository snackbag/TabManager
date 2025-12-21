package net.snackbag.tabmanager.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemGroupUtility {

    /**
     * Tries to parse an ItemGroup from an id.
     * Always returns the first entry found. If wished otherwise, please perform a search manually: {@link ItemGroups#getGroups()}
     * @param id The id of the ItemGroup you wish
     * @return The first matching entry of ItemGroup if found, otherwise null
     */
    public static @Nullable ItemGroup parse(String id) {
        return ItemGroups.getGroups()
                .stream()
                .filter(igroup -> ((ItemGroupAccessor) igroup).tabmanager$getTabKey().toString().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static void populateItemGroups() {
        List<ItemGroup> allGroups = ItemGroups.getGroups();

        allGroups.forEach(igroup -> ((ItemGroupAccessor)igroup).tabmanager$setTabKey(Registries.ITEM_GROUP.getId(igroup)));
    }

}
