package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;
import net.snackbag.tabmanager.util.CreativeMenuUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryEditComponent {

    protected final GridLayout topItemGroupRow, bottomItemGroupRow;
    protected final GridLayout tabControlGridContainer, tabControlGrid;
    //protected final ScrollContainer here or smth else scrollable
    protected final StackLayout inventoryLayout;
    protected final FlowLayout componentLayout;

    protected List<TabWidget> tabs = new ArrayList<>();
    
    protected final TextureComponent creativeInventoryTexture;
    protected final ButtonComponent editFilterButton, nextPageButton, previousPageButton;
    protected final ButtonComponent pageLabel; // Button Component as Label because it's easier to work with and look the same

    protected IconButtonComponent moveLeftButton, moveRightButton, moveUpButton, moveDownButton, toTrayButton, fromTrayButton, newPageButton, removePageButton, changeIconButton;

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

    protected int currentPage = 1;
    protected int maxPages = CreativeMenuUtility.getPageCount() - 1;
    
    public static final Identifier inventoryTextureIdentifier = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/creative_inventory.png");

    /**
     * Draws the creative inventory with the edit button on in the editor screen
     * @param textureWidth How wide the inventory should be (influences button width)
     * @param textureHeight How high the inventory should be (influences button height)
     * @param onFilterClick what happens if the filterButton is clicked
     */
    public InventoryEditComponent(int textureWidth, int textureHeight, Consumer<ButtonComponent> onFilterClick) {
        // Set sizes
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        // Calculate button sizes based on texture size
        fixedButtonWidth = (int) (textureWidth * filterButtonMultiplierW);
        fixedButtonHeight = (int) (textureHeight * filterButtonMultiplierH);

        pageSwitchWidth = (int) (textureWidth * pageSwitchButtonMultiplierW);
        pageSwitchHeight = (int) (textureHeight * pageSwitchButtonMultiplierH);

        // Initialize layouts
        topItemGroupRow = Containers.grid(Sizing.content(), Sizing.content(), 1, 5); // 1 Row, 5 Item Groups per row
        bottomItemGroupRow = Containers.grid(Sizing.content(), Sizing.content(), 1, 5); // The same thing here

        tabControlGridContainer = Containers.grid(Sizing.content(), Sizing.content(), 2, 1);
        tabControlGrid = Containers.grid(Sizing.content(), Sizing.content(), 1, 9);

        inventoryLayout = Containers.stack(Sizing.content(), Sizing.content());
        componentLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        // Initialize components
        this.creativeInventoryTexture = Components.texture(
                inventoryTextureIdentifier,
                0, 0,
                textureWidth, textureHeight,
                textureWidth, textureHeight);
        
        editFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.edit_item_masks"), onFilterClick);
        editFilterButton.zIndex(10)
                .positioning(Positioning.absolute(filterButtonPosMagicNumber, filterButtonPosMagicNumber))
                .sizing(Sizing.fixed(fixedButtonWidth), Sizing.fixed(fixedButtonHeight));

        nextPageButton = Components.button(Text.literal("->"), (btn) -> nextPage());
        nextPageButton.zIndex(10)
                .positioning(Positioning.absolute(pageSwitchButtonPosMagicNumberW + pageSwitchWidth * 2, pageSwitchButtonPosMagicNumberH))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight))
                .tooltip(Text.translatable("tabmanager.gui.edit_screen.next_page_tooltip"));

        previousPageButton = Components.button(Text.literal("<-"), (btn) -> previousPage());
        previousPageButton.zIndex(10)
                .positioning(Positioning.absolute(pageSwitchButtonPosMagicNumberW, pageSwitchButtonPosMagicNumberH))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight))
                .tooltip(Text.translatable("tabmanager.gui.edit_screen.previous_page_tooltip"));

        pageLabel = Components.button(Text.translatable("tabmanager.gui.edit_screen.page", currentPage, maxPages), (btn) -> {});
        pageLabel.active(false)
                .zIndex(10)
                .positioning(Positioning.absolute(pageSwitchButtonPosMagicNumberW + pageSwitchWidth, pageSwitchButtonPosMagicNumberH))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight));

        initTabControls();
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

        tabControlGridContainer.child(tabControlGrid, 0, 0);

        tabControlGrid.child(moveLeftButton.build(), 0, 0)
                .child(moveRightButton.build(), 0, 1)
                .child(moveUpButton.build(), 0, 2)
                .child(moveDownButton.build(), 0, 3)
                .child(toTrayButton.build(), 0, 4)
                .child(fromTrayButton.build(), 0, 5)
                .child(newPageButton.build(), 0, 6)
                .child(removePageButton.build(), 0, 7)
                .child(changeIconButton.build(), 0, 8);

        componentLayout.child(topItemGroupRow)
                .child(inventoryLayout)
                .child(bottomItemGroupRow)
                .child(tabControlGridContainer);

        updateItemGroups();

        addToParent.apply(componentLayout);
    }

    private void initTabControls() {
        moveLeftButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_left.png"), 13, 13, (btn) -> {});
        moveRightButton =   new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_right.png"), 13, 13, (btn) -> {});
        moveUpButton =      new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_up.png"), 13, 13, (btn) -> {});
        moveDownButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_down.png"), 13, 13, (btn) -> {});
        toTrayButton =      new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/to_tray.png"), 13, 13, (btn) -> {});
        fromTrayButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/from_tray.png"), 13, 13, (btn) -> {});
        newPageButton =     new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/new_page.png"), 13, 13, (btn) -> {});
        changeIconButton =  new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/change_icon.png"), 13, 13, (btn) -> {});
        removePageButton =  new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/remove_page.png"), 13, 13, (btn) -> {});
    }

    private TabWidget getTab(ItemGroup reference) {
        return new TabWidget(reference, false, (widget) -> {
            tabs.stream().filter(tab -> tab != widget).forEach(tab -> tab.setActive(false));
        });
    }

    private void nextPage() {
        if (currentPage >= maxPages) return; // Cannot go more than maxPages

        currentPage++;
        updatePageLabel();
        updateItemGroups();
    }

    private void previousPage() {
        if (currentPage <= 1) return; // Cannot go less than page 1

        currentPage--;
        updatePageLabel();
        updateItemGroups();
    }

    private void updatePageLabel() {
        pageLabel.setMessage(Text.translatable("tabmanager.gui.edit_screen.page", currentPage, maxPages));
    }

    private void updateItemGroups() {
        List<ItemGroup> groups = CreativeMenuUtility.getItemGroupsOnPage(currentPage - 1);
        clearComponent(topItemGroupRow);
        clearComponent(bottomItemGroupRow);
        tabs.clear();

        if (groups.isEmpty()) return; // Nothing to display

        for (ItemGroup displayItem : groups) {
            if (displayItem.getRow() == ItemGroup.Row.TOP) {
                TabWidget tab = getTab(displayItem);
                topItemGroupRow.child(tab.build(), 0, displayItem.getColumn());
                tabs.add(tab);
            } else if (displayItem.getRow() == ItemGroup.Row.BOTTOM) {
                TabWidget tab = getTab(displayItem);
                bottomItemGroupRow.child(tab.build(), 0, displayItem.getColumn());
                tabs.add(tab);
            } else {
                TabManagerClient.LOGGER.error("An error occurred: Couldn't determine row for ItemGroup {}", displayItem.toString());
            }
        }
    }

    private void clearComponent(ParentComponent component) {
        List<Component> components = new ArrayList<>(component.children());
        components.forEach(component::removeChild);
    }
}
