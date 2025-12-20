package net.snackbag.tabmanager.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

/**
 * Record that stores the association between an ItemGroup and its Identifier.
 */
public record ItemGroupInfo(ItemGroup itemGroup, Identifier id) {
}
