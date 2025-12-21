package net.snackbag.tabmanager.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemGroup;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.exception.ItemFilterParseException;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ItemFilter {

    private static final short SERIALIZE_VERSION = 1; // If there will ever bee improvements to the JSON structure, so it's backwards compatible using conversion methods

    private final Predicate<String> matcher;
    private final String predicateSource;
    private final Set<ItemGroup> applicableGroups = new HashSet<>();

    /**
     * Creates an ItemFilter from a regex pattern
     * @param predicate the predicate to use for matching
     * @param predicateSource the source string used to create the predicate (for serialization purposes) (either glob or regex)
     */
    public ItemFilter(Predicate<String> predicate, String predicateSource) {
        this.predicateSource = predicateSource;
        this.matcher = predicate;
    }

    public boolean matches(String id) {
        return matcher.test(id);
    }

    public Set<ItemGroup> getApplicableGroups() {
        return applicableGroups;
    }

    public void addApplicableGroup(ItemGroup group) {
        applicableGroups.add(group);
    }

    public void removeApplicableGroup(ItemGroup group) {
        applicableGroups.remove(group);
    }

    public void clearApplicableGroups() {
        applicableGroups.clear();
    }

    public void setApplicableGroups(Set<ItemGroup> groups) {
        applicableGroups.clear();
        applicableGroups.addAll(groups);
    }

    /**
     * Parses either a glob or a regex into an ItemFilter
     * @param line the glob or regex mask
     * @return the ItemFilter without any applicable {@link ItemGroup}s. Must manually apply later.
     */
    public static ItemFilter parse(String line) {
        line = line.trim();

        if (line.startsWith("regex:")) {
            String regex = line.substring("regex:".length());
            Pattern pattern = RegexCompiler.compileRegex(regex);
            return new ItemFilter(id -> pattern.matcher(id).matches(), line);
        }

        if (line.startsWith("glob:")) {
            String glob = line.substring("glob:".length());
            Pattern pattern = RegexCompiler.compileGlob(glob);
            return new ItemFilter(id -> pattern.matcher(id).matches(), line);
        }

        // Default to glob pattern
        Pattern pattern = RegexCompiler.compileGlob(line);
        return new ItemFilter(id -> pattern.matcher(id).matches(), line);
    }

    /**
     * Produces an ItemFilter from an {@link JsonObject}
     * @param filterObj The {@link JsonObject} to parse the ItemFilter from
     * @return The parsed ItemFilter
     * @see ItemFilter#serialize()
     */
    public static ItemFilter parse(JsonObject filterObj) throws ItemFilterParseException {

        // Checks
        if (!filterObj.has("serializeVersion"))
            throw new ItemFilterParseException("serializeVersion missing!");

        if (filterObj.get("serializeVersion").getAsInt() != SERIALIZE_VERSION)
            throw new ItemFilterParseException("serializeVersion mismatch!");

        if (!filterObj.has("predicateSource"))
            throw new ItemFilterParseException("predicateSource missing!");

        if (!filterObj.has("applicableGroups"))
            throw new ItemFilterParseException("applicableGroups missing!");

        String predicateSource;
        Set<ItemGroup> applicableGroups = new HashSet<>();

        predicateSource = filterObj.get("predicateSource").getAsString();

        JsonArray groupsArray = filterObj.getAsJsonArray("applicableGroups");

        groupsArray.forEach(itemGroup -> applicableGroups.add(ItemGroupUtility.parse(itemGroup.getAsString())));

        ItemFilter filter = ItemFilter.parse(predicateSource);
        filter.setApplicableGroups(applicableGroups);

        return filter;
    }

    /**
     * Produces a JsonObject from an ItemFilter
     * @return the JsonObject
     * @see ItemFilter#parse(JsonObject)
     */
    public JsonObject serialize() {
        JsonObject obj = new JsonObject();

        JsonArray groupsArray = new JsonArray();
        applicableGroups.forEach(igroup -> groupsArray.add(((ItemGroupAccessor) igroup).tabmanager$getTabKey().toString()));

        obj.addProperty("serializeVersion", SERIALIZE_VERSION);
        obj.addProperty("predicateSource", predicateSource);
        obj.add("applicableGroups", groupsArray);

        return obj;
    }
}
