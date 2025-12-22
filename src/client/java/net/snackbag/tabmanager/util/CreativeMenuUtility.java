package net.snackbag.tabmanager.util;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;

public class CreativeMenuUtility {

    @SuppressWarnings("UnstableApiUsage")
    public static int getPageCount() {
        return FabricCreativeGuiComponents.getPageCount();
    }

}
