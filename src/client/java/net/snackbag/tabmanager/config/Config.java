package net.snackbag.tabmanager.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.snackbag.tabmanager.exception.ConfigParseException;
import net.snackbag.tabmanager.util.ItemFilter;
import net.snackbag.tabmanager.util.ItemGroupUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class Config {

    private static final short SERIALIZE_VERSION = 1; // If there will ever bee improvements to the JSON structure, so it's backwards compatible using conversion methods

    public static Config INSTANCE = new Config();
    public Collection<ItemFilter> filters;

    /**
     * Serializes the config into a JsonObject
     * @return the serialized JsonObject
     */
    public JsonObject serialize() {
        JsonObject obj = new JsonObject();

        JsonArray filtersArray = new JsonArray();

        for (ItemFilter filter : filters)
            filtersArray.add(filter.serialize());

        obj.addProperty("serializeVersion", SERIALIZE_VERSION);
        obj.add("filters", filtersArray);

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

        Collection<ItemFilter> filters = new ArrayList<>();

        JsonArray filtersArray = config.getAsJsonArray("filters");
        filtersArray.forEach(item ->
            filters.add(ItemFilter.parse(item.getAsJsonObject()))
        );

        Config cfg = new Config();
        cfg.filters = filters;

        return cfg;
    }

    /**
     * Loads the given config and reloads all settings
     * @param config the config to load
     */
    public static void loadConfig(Config config) {
        Config.INSTANCE = config;

        // Re-apply filters
        ItemGroupUtility.applyFilters();

        // Reorder, hide/show, etc... here
    }

    /**
     * Loads config from a file
     * @param configFile the file to load from
     */
    public static void loadConfigFile(File configFile) throws ConfigParseException, IOException {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            byte[] fileContentBytes = fis.readAllBytes();
            String fileContent = new String(fileContentBytes);

            JsonObject configJson = JsonParser.parseString(fileContent).getAsJsonObject();

            loadConfig(Config.parse(configJson));
        }
    }

    public static void writeConfigFile(File configFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            String parsedConfig = Config.INSTANCE.serialize().toString();
            byte[] parsedConfigBytes = parsedConfig.getBytes();
            fos.write(parsedConfigBytes);
        }
    }
}
