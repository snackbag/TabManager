package net.snackbag.tabmanager.mixin.client;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroup.class)
abstract public class ItemGroupMixin implements ItemGroupAccessor {

    // SHADOWED FIELDS ---------------------------

    @Shadow @Final @Mutable
    private ItemGroup.Row row;

    @Shadow @Final @Mutable
    private int column;

    @Shadow
    private @Nullable ItemStack icon;



    // UNIQUE FIELDS -----------------------------

    @Unique
    private Identifier tabmanager$tabKey;

    @Unique
    private boolean tabmanager$isHidden = false;




    // INJECTIONS --------------------------------

    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void shouldDisplay(CallbackInfoReturnable<Boolean> cir) {
        if (tabmanager$isHidden)
            cir.setReturnValue(false);
    }




    // GETTERS / SETTERS -------------------------

    @Override
    public Identifier tabmanager$getTabKey() {
        return tabmanager$tabKey;
    }

    @Override
    public void tabmanager$setTabKey(Identifier id) {
        this.tabmanager$tabKey = id;
    }

    @Override
    public boolean tabmanager$isHidden() {
        return tabmanager$isHidden;
    }

    @Override
    public void tabmanager$setHidden(boolean hidden) {
        tabmanager$isHidden = hidden;
    }

    @Override
    public void tabmanager$setColumn(int column) {
        this.column = column;
    }

    @Override
    public void tabmanager$setRow(ItemGroup.Row row) {
        this.row = row;
    }

    @Override
    public void tabmanager$setIcon(ItemStack istack) {
        this.icon = istack;
    }
}
