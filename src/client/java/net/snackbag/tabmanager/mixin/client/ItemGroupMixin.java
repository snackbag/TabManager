package net.snackbag.tabmanager.mixin.client;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.access.AdditionalTabInfoAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroup.class)
abstract public class ItemGroupMixin implements AdditionalTabInfoAccessor {

    @Unique
    private Identifier tabmanager$tabKey;

    @Unique
    private boolean tabmanager$isHidden = false;

    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void shouldDisplay(CallbackInfoReturnable<Boolean> cir) {
        if (tabmanager$isHidden)
            cir.setReturnValue(false);
    }

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
}
