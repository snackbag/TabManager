package net.snackbag.tabmanager.mixin.client;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.snackbag.tabmanager.access.ButtonWidgetAccessor;
import net.snackbag.tabmanager.access.CreativeInventoryScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class FabricItemGroupButtonWidgetMixin {

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void tabmanager$onInit(int x, int y, FabricCreativeGuiComponents.Type type, CreativeInventoryScreen screen, CallbackInfo ci) {
        ((ButtonWidgetAccessor) this).tabmanager$setOnPress((bw) -> {
            int toPage = type == FabricCreativeGuiComponents.Type.NEXT ? screen.getCurrentPage() + 1 : screen.getCurrentPage() - 1;
            ((CreativeInventoryScreenAccessor) screen).tabmanager$setCurrentPage(toPage);
        });
    }

}
