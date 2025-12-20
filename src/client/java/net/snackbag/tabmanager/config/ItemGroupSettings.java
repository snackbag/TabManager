package net.snackbag.tabmanager.config;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.snackbag.tabmanager.util.ItemGroupInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemGroupSettings {

    public static final Set<ItemGroupInfo> ITEM_GROUPS = new HashSet<>();

    public static void populateItemGroups() {
        List<ItemGroup> allGroups = ItemGroups.getGroups();

        allGroups.forEach(igroup -> {
            ItemGroupInfo pair = new ItemGroupInfo(
                    igroup,
                    Registries.ITEM_GROUP.getId(igroup)
            );
            ITEM_GROUPS.add(pair);
        });
    }

}
