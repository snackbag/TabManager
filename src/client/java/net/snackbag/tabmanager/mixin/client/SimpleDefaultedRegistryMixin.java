package net.snackbag.tabmanager.mixin.client;

import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.snackbag.tabmanager.mixin_interface.SimpleDefaultedRegistryInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(SimpleDefaultedRegistry.class)
public class SimpleDefaultedRegistryMixin<T> implements SimpleDefaultedRegistryInterface<T> {

    @Shadow
    private RegistryEntry.Reference<T> defaultEntry;

    @Override
    public Optional<RegistryEntry.Reference<T>> tabmanager$getDefaultEntry() {
        return Optional.ofNullable(this.defaultEntry);
    }
}
