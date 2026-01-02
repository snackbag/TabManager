package net.snackbag.tabmanager.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.search.SearchManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;
import net.snackbag.tabmanager.ui.screen.EditScreen;
import net.snackbag.tabmanager.util.ItemGroupUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeInventoryScreen.class)
abstract public class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    @Unique
    private TexturedButtonWidget editButton;

    @Unique private static final int EDIT_BUTTON_WIDTH_HEIGHT = 20;
    @Unique private static final int PADDING_TO_CREATIVE_INV = 5;

    @Unique private static final ButtonTextures EDIT_BUTTON_TEXTURES = new ButtonTextures(Identifier.of(TabManagerClient.MOD_ID, "widget/edit_button"), Identifier.of(TabManagerClient.MOD_ID, "widget/edit_button_highlighted"));

    private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    /**
     * Adds the edit button to the creative inventory screen.
     */
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setMaxLength(I)V")) // Execute inside the if-statement in init
    private void tabmanager$onInit(CallbackInfo ci) {
        // Render on top right above the inventory
        int xPos = this.x + this.backgroundWidth - EDIT_BUTTON_WIDTH_HEIGHT;
        int yPos = this.y - EDIT_BUTTON_WIDTH_HEIGHT - PADDING_TO_CREATIVE_INV - 24;

        this.editButton = new TexturedButtonWidget(
                xPos,
                yPos,
                EDIT_BUTTON_WIDTH_HEIGHT,
                EDIT_BUTTON_WIDTH_HEIGHT,
                EDIT_BUTTON_TEXTURES,
                (button) -> tabmanager$openEditScreen(),
                Text.translatable("tabmanager.gui.edit_button.tooltip")
        );

        this.editButton.setTooltip(Tooltip.of(Text.translatable("tabmanager.gui.edit_button.tooltip")));

        this.addDrawableChild(editButton);
    }

    @Inject(method = "populateDisplay", at = @At("TAIL"))
    private void tabmanager$applyConfig(SearchManager searchManager, FeatureSet enabledFeatures, boolean showOperatorTab, RegistryWrapper.WrapperLookup registryLookup, CallbackInfoReturnable<Boolean> cir) {
        // Apply config on inventory open
        ItemGroupUtility.reloadItemGroups();
    }

    @Unique
    private void tabmanager$openEditScreen() {
        // Placeholder for future implementation
        MinecraftClient.getInstance().setScreen(new EditScreen());
    }

}
