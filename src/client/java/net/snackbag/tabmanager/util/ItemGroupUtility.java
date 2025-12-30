package net.snackbag.tabmanager.util;

import com.google.gson.JsonObject;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemGroupUtility {

    public static final int SERIALIZE_VERSION = 1;

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

    public static void applyFilters() {
        List<ItemGroup> allGroups = ItemGroups.getGroups();

        allGroups.forEach(igroup -> {
            ItemGroupAccessor accessor = (ItemGroupAccessor) igroup;
            accessor.tabmanager$resetDisplayItems();

            // Apply all filters that apply to this group
            Config.INSTANCE.filters.stream()
                    .filter(filter -> filter.getApplicableGroups().contains(igroup))
                    .forEach(accessor::tabmanager$applyFilterDisplayItems);
        });
    }

    public static JsonObject serialize(ItemGroup group) {
        JsonObject serialized = new JsonObject();

        serialized.addProperty("serializeVersion", SERIALIZE_VERSION);
        serialized.addProperty("id", ((ItemGroupAccessor)group).tabmanager$getTabKey().toString());
        serialized.addProperty("icon", group.getIcon().getItem().toString());
        serialized.addProperty("column", group.getColumn());
        serialized.addProperty("row", group.getRow().ordinal());
        serialized.addProperty("page", ((ItemGroupAccessor)group).tabmanager$getPage());
        serialized.addProperty("hidden", ((ItemGroupAccessor)group).tabmanager$isHidden());

        return serialized;
    }

    /**
     * Applies a serialized ItemGroup JsonObject to an ItemGroup
     * If the ItemGroup could not be found, nothing will happen
     * @param obj the serialized ItemGroup JsonObject
     */
    public static void applySerialized(JsonObject obj) {
        ItemGroup group = parse(obj.get("id").getAsString());

        if (group == null) return;

        ItemGroupAccessor accessor = (ItemGroupAccessor) group;

        if (obj.has("icon")) {
            Identifier iconId = Identifier.tryParse(obj.get("icon").getAsString());
            if (iconId != null) {
                Item icon = Registries.ITEM.get(iconId);

                //noinspection OptionalGetWithoutIsPresent
                if (icon != Registries.ITEM.getDefaultEntry().get().value()) {
                    accessor.tabmanager$setIcon(icon.getDefaultStack());
                }
            }
        }

        if (obj.has("column"))
            accessor.tabmanager$setColumn(obj.get("column").getAsInt());

        if (obj.has("row"))
            accessor.tabmanager$setRow(ItemGroup.Row.values()[obj.get("row").getAsInt()]);

        if (obj.has("page"))
            accessor.tabmanager$setPage(obj.get("page").getAsInt());

        if (obj.has("hidden"))
            accessor.tabmanager$setHidden(obj.get("hidden").getAsBoolean());
    }
}
