package net.snackbag.tabmanager.ui.screen;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;
import org.jetbrains.annotations.NotNull;

public class EditScreen extends BaseOwoScreen<FlowLayout> {

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        final int controlPanelWidth = 200;

        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP);

        FlowLayout stageLayout = Containers.horizontalFlow(Sizing.fill(), Sizing.fill());

        FlowLayout controlPanel = Containers.verticalFlow(Sizing.fixed(controlPanelWidth), Sizing.fill());
        FlowLayout canvasPanel = Containers.horizontalFlow(Sizing.expand(), Sizing.fill());

        controlPanel.surface(Surface.DARK_PANEL);
        controlPanel.margins(Insets.of(5));

        canvasPanel.verticalAlignment(VerticalAlignment.CENTER);
        canvasPanel.horizontalAlignment(HorizontalAlignment.CENTER);

        drawConfigControls(controlPanel, controlPanelWidth);
        drawSaveCancelControls(controlPanel, controlPanelWidth);

        drawCreativeContainer(canvasPanel, 195, 127);

        stageLayout
                .child(controlPanel)
                .child(canvasPanel);

        rootComponent.child(stageLayout);
    }

    /**
     * Draws the configuration controls for the editor
     * @param rootComponent The component to attach these controls to
     * @param width The width of these controls
     */
    private void drawConfigControls(FlowLayout rootComponent, final int width) {
        FlowLayout confCtrlContainer = Containers.verticalFlow(Sizing.fixed(width), Sizing.content())
                .gap(5);

        confCtrlContainer.padding(Insets.of(5));

        ButtonComponent loadConfigButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.import_config_button"), (btn) -> {});
        ButtonComponent saveConfigButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.export_config_button"), (btn) -> {});
        ButtonComponent newConfigButton  = Components.button(Text.translatable("tabmanager.gui.edit_screen.new_config_button"),    (btn) -> {});

        TextBoxComponent configNameBox = Components.textBox(Sizing.fill());
        configNameBox.setEditable(false);

        confCtrlContainer
                .child(loadConfigButton)
                .child(saveConfigButton)
                .child(newConfigButton)
                .child(configNameBox);

        confCtrlContainer.forEachDescendant(c -> c.sizing(Sizing.fill(), Sizing.content()));

        rootComponent.child(confCtrlContainer);
    }

    /**
     * Draws the "Save" and "Close" controls for the editor
     * @param rootComponent The component to attach the buttons on
     * @param width The width of these controls
     */
    private void drawSaveCancelControls(FlowLayout rootComponent, final int width) {
        FlowLayout scCtrlContainer = Containers.verticalFlow(Sizing.fixed(width), Sizing.expand())
                .gap(5);

        scCtrlContainer.verticalAlignment(VerticalAlignment.BOTTOM);
        scCtrlContainer.padding(Insets.of(5));

        ButtonComponent saveButton =  Components.button(Text.translatable("tabmanager.gui.edit_screen.save_button"),   (btn) -> {});
        ButtonComponent closeButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.close_button"), (btn) -> {});

        saveButton.sizing(Sizing.fill(), Sizing.content());
        closeButton.sizing(Sizing.fill(), Sizing.content());

        scCtrlContainer
                .child(closeButton)
                .child(saveButton);

        rootComponent.child(scCtrlContainer);
    }

    /**
     * Draws the creative inventory with the edit button on in the editor screen
     * @param rootComponent The component to attach the creative screen on
     * @param textureWidth How wide the inventory should be (influences button width)
     * @param textureHeight How high the inventory should be (influences button height)
     */
    private void drawCreativeContainer(FlowLayout rootComponent, final int textureWidth, final int textureHeight) {
        final float buttonMultiplierW = 162/195f; // Calculates the width of the button. Change these two if you change the creative inventory texture.
        final float buttonMultiplierH = 90/127f;

        final int buttonWidth = (int) (textureWidth * buttonMultiplierW);
        final int buttonHeight = (int) (textureHeight * buttonMultiplierH);

        StackLayout componentLayout = Containers.stack(Sizing.content(), Sizing.content());

        TextureComponent creativeInventoryTexture = Components.texture(
                Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/creative_inventory.png"),
                0, 0,
                textureWidth, textureHeight,
                textureWidth, textureHeight);

        ButtonComponent itemMasksButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.edit_item_masks"), (btn) -> {});

        itemMasksButton.zIndex(10);
        itemMasksButton.positioning(Positioning.absolute(8, 8));
        itemMasksButton.sizing(Sizing.fixed(buttonWidth), Sizing.fixed(buttonHeight));

        componentLayout.child(creativeInventoryTexture);
        componentLayout.child(itemMasksButton);

        rootComponent.child(componentLayout);
    }
}