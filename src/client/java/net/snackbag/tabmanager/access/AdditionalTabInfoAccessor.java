package net.snackbag.tabmanager.access;

import net.minecraft.util.Identifier;

public interface AdditionalTabInfoAccessor {

    /**
     * Gets the unique Identifier key for this tab.
     * @return The tab's unique Identifier.
     */
    Identifier tabmanager$getTabKey();

    /**
     * Sets the unique Identifier key for this tab.
     * @param id The tab's unique Identifier.
     */
    void tabmanager$setTabKey(Identifier id);

    /**
     * Checks if the tab is hidden.
     * @return True if the tab is hidden, false otherwise.
     */
    boolean tabmanager$isHidden();

    /**
     * Sets the hidden status of the tab.
     * @param hidden True to hide the tab, false to show it.
     */
    void tabmanager$setHidden(boolean hidden);
}
