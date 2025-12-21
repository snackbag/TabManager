package net.snackbag.tabmanager.config;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.snackbag.tabmanager.access.ItemGroupAccessor;

import java.util.List;

public class ItemGroupSettings {

    public static void populateItemGroups() {
        List<ItemGroup> allGroups = ItemGroups.getGroups();

        allGroups.forEach(igroup -> ((ItemGroupAccessor)igroup).tabmanager$setTabKey(Registries.ITEM_GROUP.getId(igroup)));
    }
}
