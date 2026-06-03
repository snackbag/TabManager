package net.snackbag.tabmanager.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.search.SearchManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.ui.screen.EditScreen;
import net.snackbag.tabmanager.ui.vanilla.EditInventoryButton;
import net.snackbag.tabmanager.util.ItemGroupUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeInventoryScreen.class)
abstract public class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    @Unique private static final int PADDING_TO_CREATIVE_INV = 5;

    private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    /**
     * Adds the edit button to the creative inventory screen.
     */
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setMaxLength(I)V")) // Execute inside the if-statement in init
    private void tabmanager$onInit(CallbackInfo ci) {
        // Render on top right above the inventory
        int xPos = this.x + this.backgroundWidth - EditInventoryButton.EDIT_BUTTON_WIDTH_HEIGHT;
        int yPos = this.y - EditInventoryButton.EDIT_BUTTON_WIDTH_HEIGHT - PADDING_TO_CREATIVE_INV - 24;

        var editInventoryButton = new EditInventoryButton(
                xPos,
                yPos,
                (button) -> tabmanager$openEditScreen()
        );

        this.addDrawableChild(editInventoryButton);
    }

    /*? if =1.21.1 {*/
    @Inject(method = "populateDisplay", at = @At("TAIL"))
    private void tabmanager$applyConfig(SearchManager searchManager, FeatureSet enabledFeatures, boolean showOperatorTab, RegistryWrapper.WrapperLookup registryLookup, CallbackInfoReturnable<Boolean> cir) {
        // Apply config on inventory open
        ItemGroupUtility.reloadItemGroups();
    }
    /*?} elif =1.20.1 {*/
    /*@Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemGroups;updateDisplayContext(Lnet/minecraft/resource/featuretoggle/FeatureSet;ZLnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Z"
            )
    )
    private void tabmanager$onDisplayContext(CallbackInfo ci) {
        ItemGroupUtility.reloadItemGroups();
    }

    @Inject(
            method = "updateDisplayParameters",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemGroups;updateDisplayContext(Lnet/minecraft/resource/featuretoggle/FeatureSet;ZLnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Z"
            )
    )
    private void tabmanager$onUpdateDisplayParameters(CallbackInfo ci) {
        ItemGroupUtility.reloadItemGroups();
    }
    *//*?}*/

    @Unique
    private void tabmanager$openEditScreen() {
        // Placeholder for future implementation
        MinecraftClient.getInstance().setScreen(new EditScreen());
    }

}
