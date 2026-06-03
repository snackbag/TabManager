package net.snackbag.tabmanager.mixin_interface;

import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public interface SimpleDefaultedRegistryInterface<T> {
    Optional<RegistryEntry.Reference<T>> tabmanager$getDefaultEntry();
}
