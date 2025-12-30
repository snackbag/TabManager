package net.snackbag.tabmanager.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.snackbag.tabmanager.exception.ConfigParseException;
import net.snackbag.tabmanager.util.ItemFilter;
import net.snackbag.tabmanager.util.ItemGroupUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static final short SERIALIZE_VERSION = 1; // If there will ever be improvements to the JSON structure, so it's backwards compatible using conversion methods

    // Always use INSTANCE if the config should be used!
    // Make new instance only for storing Configs!
    public static Config INSTANCE = new Config();
    public List<ItemFilter> filters = new ArrayList<>();
    public byte fakePages; // Pages to fake in the creative menu to allow for empty and more pages.

    private String name = "default";

    /**
     * Serializes the config into a JsonObject
     * @return the serialized JsonObject
     */
    public JsonObject serialize() {
        JsonObject obj = new JsonObject();

        JsonArray filtersArray = new JsonArray();
        JsonArray itemGroups = new JsonArray();

        for (ItemFilter filter : filters)
            filtersArray.add(filter.serialize());

        obj.addProperty("serializeVersion", SERIALIZE_VERSION);
        obj.addProperty("name", this.name);
        obj.addProperty("fakePages", this.fakePages);
        obj.add("filters", filtersArray);

        for (ItemGroup itemGroup : ItemGroups.getGroups().stream().filter(ig -> !ig.isSpecial()).toList())
            itemGroups.add(ItemGroupUtility.serialize(itemGroup));

        obj.add("itemGroups", itemGroups);

        return obj;
    }

    /**
     * Parses a JsonObject into a Config
     * @param config the JsonObject to parse
     * @return the parsed Config
     * @throws ConfigParseException if the config is broken or unsupported
     */
    public static Config parse(JsonObject config) throws ConfigParseException {

        if (!config.has("serializeVersion"))
            throw new ConfigParseException("Broken config: Missing config version");

        if (config.get("serializeVersion").getAsShort() != SERIALIZE_VERSION) {
            throw new ConfigParseException("Unsupported config version");
            // Otherwise, we would do conversion here
        }

        List<ItemFilter> filters = new ArrayList<>();

        JsonArray filtersArray = config.getAsJsonArray("filters");
        filtersArray.forEach(item ->
            filters.add(ItemFilter.parse(item.getAsJsonObject()))
        );

        JsonArray itemGroupsArray = config.getAsJsonArray("itemGroups");
        itemGroupsArray.forEach(item ->
            ItemGroupUtility.applySerialized(item.getAsJsonObject())
        );

        Config cfg = new Config();
        cfg.filters = filters;
        cfg.setName(config.has("name") ? config.get("name").getAsString() : "default");
        cfg.fakePages = config.has("fakePages") ? config.get("fakePages").getAsByte() : 0;

        return cfg;
    }

    public static void reload() {
        // Re-apply filters
        ItemGroupUtility.applyFilters();
    }

    /**
     * Loads the given config and reloads all settings
     * @param config the config to load
     */
    public static void loadConfig(Config config) {
        Config.INSTANCE = config;
        reload();
    }

    /**
     * Loads config from a file
     * @param configFile the file to load from
     */
    public static void loadConfigFile(File configFile) throws ConfigParseException, IOException {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            byte[] fileContentBytes = fis.readAllBytes();

            if (fileContentBytes.length == 0) return; // Nothing to read
            
            String fileContent = new String(fileContentBytes);

            JsonObject configJson = JsonParser.parseString(fileContent).getAsJsonObject();

            loadConfig(Config.parse(configJson));
        }
    }

    /**
     * Writes current config to given file
     * @param configFile the file to write to
     * @throws IOException If file was not found
     */
    public static void writeConfigFile(File configFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            String parsedConfig = Config.INSTANCE.serialize().toString();
            byte[] parsedConfigBytes = parsedConfig.getBytes();
            fos.write(parsedConfigBytes);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
