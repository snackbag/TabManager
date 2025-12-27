package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;

import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryEditComponent {

    protected final GridLayout topItemGroupRow, bottomItemGroupRow;
    protected final StackLayout inventoryLayout;
    protected final FlowLayout componentLayout;
    
    protected final TextureComponent creativeInventoryTexture;
    protected final ButtonComponent editFilterButton, nextPageButton, previousPageButton;
    protected final ButtonComponent pageLabel; // Button Component as Label because it's easier to work with and look the same

    protected final int textureWidth, textureHeight;
    protected final int fixedButtonWidth, fixedButtonHeight;
    protected final int pageSwitchWidth, pageSwitchHeight;
    
    public static final float filterButtonMultiplierW = 162/195f; // The original width multiplier of the image to calculate button width
    public static final float filterButtonMultiplierH = 90/127f;  // The original height multiplier of the image to calculate button height

    public static final float pageSwitchButtonMultiplierW = 54/195f;
    public static final float pageSwitchButtonMultiplierH = 18/127f;

    // Magic numbers because positioning is fucking hard and I don't feel like investing brain power when this also does it
    public static final int filterButtonPosMagicNumber = 8;
    public static final int pageSwitchButtonPosMagicNumberW = 8;
    public static final int pageSwitchButtonPosMagicNumberH = 102;
    
    public static final Identifier inventoryTextureIdentifier = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/creative_inventory.png");
    public static final Identifier tabTextureIdentifier = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/tab.png");

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
        
        fixedButtonWidth = (int) (textureWidth * filterButtonMultiplierW);
        fixedButtonHeight = (int) (textureHeight * filterButtonMultiplierH);

        pageSwitchWidth = (int) (textureWidth * pageSwitchButtonMultiplierW);
        pageSwitchHeight = (int) (textureHeight * pageSwitchButtonMultiplierH);
        
        this.creativeInventoryTexture = Components.texture(
                inventoryTextureIdentifier,
                0, 0,
                textureWidth, textureHeight,
                textureWidth, textureHeight);
        
        editFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.edit_item_masks"), onFilterClick);
        editFilterButton.zIndex(10)
                .positioning(Positioning.absolute(filterButtonPosMagicNumber, filterButtonPosMagicNumber))
                .sizing(Sizing.fixed(fixedButtonWidth), Sizing.fixed(fixedButtonHeight));

        nextPageButton = Components.button(Text.literal("<-"), (btn) -> nextPage());
        nextPageButton.zIndex(10)
                .positioning(Positioning.absolute(pageSwitchButtonPosMagicNumberW, pageSwitchButtonPosMagicNumberH))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight));

        previousPageButton = Components.button(Text.literal("->"), (btn) -> previousPage());
        previousPageButton.zIndex(10)
                .positioning(Positioning.absolute(pageSwitchButtonPosMagicNumberW + pageSwitchWidth * 2, pageSwitchButtonPosMagicNumberH))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight));

        pageLabel = Components.button(Text.translatable("tabmanager.gui.edit_screen.page", 5, 5), (btn) -> {});
        pageLabel.active(false)
                .zIndex(10)
                .positioning(Positioning.absolute(pageSwitchButtonPosMagicNumberW + pageSwitchWidth, pageSwitchButtonPosMagicNumberH))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight));
    }

    /**
     * Builds the component
     */
    public void build(Function<FlowLayout, FlowLayout> addToParent) {
        inventoryLayout.child(creativeInventoryTexture)
                .child(editFilterButton)
                .child(nextPageButton)
                .child(previousPageButton)
                .child(pageLabel);

        componentLayout.child(topItemGroupRow)
                .child(inventoryLayout)
                .child(bottomItemGroupRow);

        topItemGroupRow.child(getTab(Blocks.WHITE_TERRACOTTA.asItem().getDefaultStack()), 0, 0);

        addToParent.apply(componentLayout);
    }

    private StackLayout getTab(ItemStack displayItem) {
        StackLayout tabLayout = Containers.stack(Sizing.content(), Sizing.content());
        TextureComponent tab = Components.texture(tabTextureIdentifier, 0, 0, 26, 26, 26, 26);

        ItemComponent item = Components.item(displayItem);
        item.zIndex(10);
        item.positioning(Positioning.relative(50, 50));

        tabLayout.child(tab).child(item);

        return tabLayout;
    }

    private void nextPage() {

    }

    private void previousPage() {

    }
}
