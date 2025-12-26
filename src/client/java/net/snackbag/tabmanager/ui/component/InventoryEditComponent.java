package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;

import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryEditComponent<R extends ParentComponent> {

    protected final GridLayout topItemGroupRow, bottomItemGroupRow;
    protected final StackLayout inventoryLayout;
    protected final FlowLayout componentLayout;
    
    protected final TextureComponent creativeInventoryTexture;
    protected final ButtonComponent editFilterButton;
    
    protected final int textureWidth, textureHeight;
    protected final int buttonWidth, buttonHeight;
    
    public static final float buttonMultiplierW = 162/195f; // The original width multiplier of the image to calculate button width
    public static final float buttonMultiplierH = 90/127f;  // The original height multiplier of the image to calculate button height
    
    public static final int buttonPositioningMagicNumber = 8;
    
    public static final Identifier inventoryTextureIdentifier = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/creative_inventory.png");

    /**
     * Draws the creative inventory with the edit button on in the editor screen
     * @param textureWidth How wide the inventory should be (influences button width)
     * @param textureHeight How high the inventory should be (influences button height)
     * @param onFilterClick what happens if the filterButton is clicked
     */
    public InventoryEditComponent(int textureWidth, int textureHeight, Consumer<ButtonComponent> onFilterClick) {
        topItemGroupRow = Containers.grid(Sizing.content(), Sizing.content(), 1, 5); // 1 Row, 5 Item Groups per row
        bottomItemGroupRow = Containers.grid(Sizing.content(), Sizing.content(), 1, 5); // The same thing here
        inventoryLayout = Containers.stack(Sizing.content(), Sizing.content());
        componentLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());
        
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        
        buttonWidth = (int) (textureWidth * buttonMultiplierW);
        buttonHeight = (int) (textureHeight * buttonMultiplierH);
        
        this.creativeInventoryTexture = Components.texture(
                inventoryTextureIdentifier,
                0, 0,
                textureWidth, textureHeight,
                textureWidth, textureHeight);
        
        this.editFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.edit_item_masks"), onFilterClick);
        editFilterButton.zIndex(10);
        editFilterButton.positioning(Positioning.absolute(buttonPositioningMagicNumber, buttonPositioningMagicNumber));
        editFilterButton.sizing(Sizing.fixed(buttonWidth), Sizing.fixed(buttonHeight));
    }

    /**
     * Builds the component
     */
    public void build(Function<FlowLayout, FlowLayout> addToParent) {
        inventoryLayout.child(creativeInventoryTexture);
        inventoryLayout.child(editFilterButton);

        componentLayout.child(topItemGroupRow);
        componentLayout.child(inventoryLayout);
        componentLayout.child(bottomItemGroupRow);

        addToParent.apply(componentLayout);
    }


}
