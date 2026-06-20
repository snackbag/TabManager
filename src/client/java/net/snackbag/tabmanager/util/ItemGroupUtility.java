package net.snackbag.tabmanager.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.mixin_interface.SimpleDefaultedRegistryInterface;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemGroupUtility {

    public static final int SERIALIZE_VERSION = 1;

    // Snapshot of all vanilla ItemGroups for resetting purposes
    public static List<VanillaSnapshot> VANILLA_GROUPS = null;

    /**
     * A snapshot of a vanilla ItemGroup's properties
     * @param id The id of the ItemGroup
     * @param page The page of the ItemGroup
     * @param column The column of the ItemGroup
     * @param rowOrdinal The row ordinal of the ItemGroup
     * @param hidden Whether the ItemGroup is hidden
     * @param iconId The icon id of the ItemGroup
     **/
    public record VanillaSnapshot(String id, int page, int column, int rowOrdinal, boolean hidden, Identifier iconId) { }

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

    @SuppressWarnings("unchecked")
    public static Optional<RegistryEntry.Reference<Item>> getDefaultItemEntries() {
        return ((SimpleDefaultedRegistryInterface<Item>) Registries.ITEM).tabmanager$getDefaultEntry();
    }

    public static Item getDefaultItemOrFallback() {
        var defaultEntries = getDefaultItemEntries();

        if (defaultEntries.isEmpty())
            return Items.BARRIER;

        return getDefaultItemEntries().get().value();
    }

    /**
     * Populates all ItemGroups with their respective tab keys
     */
    public static void populateItemGroups() {
        List<ItemGroup> allGroups = ItemGroups.getGroups();

        allGroups.forEach(igroup -> ((ItemGroupAccessor)igroup).tabmanager$setTabKey(Registries.ITEM_GROUP.getId(igroup)));
    }

    /**
     * Applies all filters from the config to all ItemGroups
     */
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

    /**
     * Reloads all ItemGroups from the config
     */
    public static void reloadItemGroups() {
        if (VANILLA_GROUPS == null) { // First time initialization; save current vanilla state
            VANILLA_GROUPS = ItemGroups.getGroups()
                    .stream()
                    .filter(igroup -> !igroup.isSpecial())
                    .map(igroup -> {
                        ItemGroupAccessor acc = (ItemGroupAccessor) igroup;
                        Identifier iconId = null;
                        if (igroup.getIcon() != null) {
                            Item iconItem = igroup.getIcon().getItem();

                            // If iconItem is not the placeholder, set iconId to item, otherwise keep as null
                            if (iconItem != getDefaultItemOrFallback()) {
                                iconId = Registries.ITEM.getId(iconItem);
                            }
                        }
                        return new VanillaSnapshot(
                                acc.tabmanager$getTabKey().toString(),
                                acc.tabmanager$getPage(),
                                igroup.getColumn(),
                                igroup.getRow().ordinal(),
                                acc.tabmanager$isHidden(),
                                iconId
                        );
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        Config.INSTANCE.itemGroups.forEach(igroup -> ItemGroupUtility.applySerialized(igroup.getAsJsonObject()));
    }

    /**
     * Saves all ItemGroups to the config
     */
    public static void saveItemGroupsToConfig() {
        if (Config.INSTANCE == null) return;

        JsonArray arr = new JsonArray();
        ItemGroups.getGroups()
                .stream()
                .filter(igroup -> !igroup.isSpecial())
                .forEach(igroup -> arr.add(serialize(igroup)));

        Config.INSTANCE.itemGroups = arr;
    }

    /**
     * Resets all ItemGroups to their vanilla state
     */
    public static void resetItemGroups() {
        if (VANILLA_GROUPS == null) return;

        VANILLA_GROUPS.forEach(snapshot -> {
            ItemGroup group = ItemGroupUtility.parse(snapshot.id);
            if (group == null) return;

            ItemGroupAccessor match = (ItemGroupAccessor) group;

            match.tabmanager$setPage(snapshot.page);
            match.tabmanager$setColumn(snapshot.column);
            match.tabmanager$setRow(ItemGroup.Row.values()[snapshot.rowOrdinal]);
            match.tabmanager$setHidden(snapshot.hidden);

            if (snapshot.iconId != null) {
                Item icon = Registries.ITEM.get(snapshot.iconId);
                match.tabmanager$setIcon(icon.getDefaultStack());
            } else {
                // Set to default icon if null
                match.tabmanager$setIcon(getDefaultItemOrFallback().getDefaultStack());
            }
        });
    }

    /**
     * Serializes an ItemGroup into a JsonObject
     * @param group the ItemGroup to serialize
     * @return the serialized JsonObject
     */
    public static JsonObject serialize(ItemGroup group) {
        JsonObject serialized = new JsonObject();

        serialized.addProperty("serializeVersion", SERIALIZE_VERSION);
        serialized.addProperty("id", ((ItemGroupAccessor)group).tabmanager$getTabKey().toString());
        serialized.addProperty("page", ((ItemGroupAccessor)group).tabmanager$getPage());
        serialized.addProperty("icon", group.getIcon().getItem().toString());
        serialized.addProperty("column", group.getColumn());
        serialized.addProperty("row", group.getRow().ordinal());
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

                if (icon != getDefaultItemOrFallback()) {
                    accessor.tabmanager$setIcon(icon.getDefaultStack());
                }
            }
        }

        if (obj.has("page"))
            accessor.tabmanager$setPage(obj.get("page").getAsInt());

        if (obj.has("column")) {
            accessor.tabmanager$setColumn(obj.get("column").getAsInt());
        }

        if (obj.has("row")) {
            accessor.tabmanager$setRow(ItemGroup.Row.values()[obj.get("row").getAsInt()]);
        }


        if (obj.has("hidden"))
            accessor.tabmanager$setHidden(obj.get("hidden").getAsBoolean());
    }
}
