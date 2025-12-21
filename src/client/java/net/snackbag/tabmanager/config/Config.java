package net.snackbag.tabmanager.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.snackbag.tabmanager.exception.ConfigParseException;
import net.snackbag.tabmanager.util.ItemFilter;

import java.util.ArrayList;
import java.util.Collection;

public class Config {

    public static void populateItemGroups() {
        List<ItemGroup> allGroups = ItemGroups.getGroups();
    public static final Config INSTANCE = new Config();
    public Collection<ItemFilter> filters;

        JsonArray filtersArray = config.getAsJsonArray("filters");
        filtersArray.forEach(item ->
            filters.add(ItemFilter.parse(item.getAsJsonObject()))
        );

        Config cfg = new Config();
        cfg.filters = filters;

        return cfg;
    }
}
