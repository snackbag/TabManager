package net.snackbag.tabmanager.mixin.client;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.snackbag.tabmanager.util.CreativeMenuUtility.getPageCount;

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
abstract public class ItemGroupButtonWidgetMixin {

    /**
     * Allows the buttons to be displayed on fake pages.
     */
    @Redirect(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;hasAdditionalPages()Z"
            )
    )
    private boolean tabmanager$redirectHasAdditionalPages(CreativeInventoryScreen creativeInventoryScreen) {
        return getPageCount() > 1;
    }

}
