package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.util.CreativeMenuUtility;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryEditComponent {

    private static final int ITEM_GROUPS_PER_ROW = 5;

    protected final GridLayout topItemGroupRow, bottomItemGroupRow;
    protected final GridLayout tabControlGridContainer, tabControlGrid;
    //protected final ScrollContainer here or smth else scrollable
    protected final StackLayout inventoryLayout;
    protected final FlowLayout inventoryContainerLayout;
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
    protected int maxPages = CreativeMenuUtility.getPageCount();
    
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

        topItemGroupRow.sizing(Sizing.fixed(TabWidget.TAB_WIDTH * 5), Sizing.content());
        bottomItemGroupRow.sizing(Sizing.fixed(TabWidget.TAB_WIDTH * 5), Sizing.content());

        tabControlGridContainer = Containers.grid(Sizing.content(), Sizing.content(), 2, 1);
        tabControlGrid = Containers.grid(Sizing.content(), Sizing.content(), 1, 9);

        tabControlGridContainer.surface(Surface.DARK_PANEL).padding(Insets.of(5));

        inventoryLayout = Containers.stack(Sizing.content(), Sizing.content());
        inventoryContainerLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());
        componentLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        componentLayout.horizontalAlignment(HorizontalAlignment.CENTER);
        componentLayout.gap(10);

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
                .child(changeIconButton.build(), 0, 6)
                .child(newPageButton.build(), 0, 7)
                .child(removePageButton.build(), 0, 8);

        inventoryContainerLayout.child(topItemGroupRow)
                .child(inventoryLayout)
                .child(bottomItemGroupRow);

        componentLayout.child(inventoryContainerLayout)
                .child(tabControlGridContainer);

        updatePageCount();
        updateItemGroups();
        updateButtons();

        addToParent.apply(componentLayout);
    }

    private void initTabControls() {
        moveLeftButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_left.png"), 13, 13, (btn) -> changeColumn(false));
        moveRightButton =   new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_right.png"), 13, 13, (btn) -> changeColumn(true));
        moveUpButton =      new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_up.png"), 13, 13, (btn) -> {});
        moveDownButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_down.png"), 13, 13, (btn) -> {});
        toTrayButton =      new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/to_tray.png"), 13, 13, (btn) -> {});
        fromTrayButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/from_tray.png"), 13, 13, (btn) -> {});
        changeIconButton =  new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/change_icon.png"), 13, 13, (btn) -> {});
        newPageButton =     new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/new_page.png"), 13, 13, (btn) -> addPage());
        removePageButton =  new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/remove_page.png"), 13, 13, (btn) -> removePage());

        moveLeftButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.move_left_tooltip"));
        moveRightButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.move_right_tooltip"));
        moveUpButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.move_up_tooltip"));
        moveDownButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.move_down_tooltip"));
        toTrayButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.to_tray_tooltip"));
        fromTrayButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.from_tray_tooltip"));
        newPageButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.new_page_tooltip"));
        changeIconButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.change_icon_tooltip"));
        removePageButton.tooltip(Text.translatable("tabmanager.gui.edit_screen.control.remove_page_tooltip"));
    }

    private TabWidget getTab(ItemGroup reference) {
        return new TabWidget(reference, false, (widget) -> {
            tabs.stream().filter(tab -> tab != widget).forEach(tab -> tab.setActive(false));
            updateButtons(); // Update buttons based on selected tab
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

    /**
     * Updates the state of the tab control buttons based on the selected tab
     */
    private void updateButtons() {
        updatePageButtons();
        updateTabButtons();
    }

    /**
     * Updates the state of the tab control buttons based on the selected tab
     */
    private void updateTabButtons() {
        TabWidget selectedTab = getSelectedTab();

        if (selectedTab == null) {
            moveLeftButton.setActive(false);
            moveRightButton.setActive(false);
            moveUpButton.setActive(false);
            moveDownButton.setActive(false);
            toTrayButton.setActive(false);
            fromTrayButton.setActive(false);
            changeIconButton.setActive(false);
            return;
        }; // No tab selected, do nothing

        moveLeftButton.setActive(selectedTab.reference.getColumn() != 0);
        moveRightButton.setActive(selectedTab.reference.getColumn() != ITEM_GROUPS_PER_ROW - 1);
        moveUpButton.setActive(selectedTab.reference.getRow() != ItemGroup.Row.TOP);
        moveDownButton.setActive(selectedTab.reference.getRow() != ItemGroup.Row.BOTTOM);
        toTrayButton.setActive(!selectedTab.isInTray());
        fromTrayButton.setActive(selectedTab.isInTray());
        changeIconButton.setActive(true);
    }

    /**
     * Updates the state of the page control buttons based on fake pages
     */
    private void updatePageButtons() {
        removePageButton.setActive(Config.INSTANCE.fakePages > 0);
    }

    private void updatePageCount() {
        maxPages = CreativeMenuUtility.getPageCount();
        updatePageLabel();
        updatePageButtons();
    }

    /**
     * Returns the currently selected tab
     */
    private @Nullable TabWidget getSelectedTab() {
        return tabs.stream().filter(TabWidget::isActive).findFirst().orElse(null);
    }

    /**
     * Clears all children from a ParentComponent
     * @param component the ParentComponent to clear
     */
    private void clearComponent(ParentComponent component) {
        List<Component> components = new ArrayList<>(component.children());
        components.forEach(component::removeChild);
    }



    // LOGIC FOR BUTTON ACTIONS --------------------------------------
    // ---------------------------------------------------------------

    /**
     * Appends a new fake page
     */
    private void addPage() {
        Config.INSTANCE.fakePages++;
        updatePageCount();
    }

    /**
     * Removes last page if possible and moves tabs from that page to free spots on other pages
     */
    private void removePage() {
        if (Config.INSTANCE.fakePages <= 0) return; // Cannot go below 0 fake pages

        // Move tabs from page to free page if current page is the last page
        List<ItemGroup> groupsToMove = ItemGroups.getGroupsToDisplay()
                .stream()
                .filter(o -> {
                    int page = ((ItemGroupAccessor) o).tabmanager$getPage();
                    return page == maxPages - 1; // -1 because pages are 0-indexed
                })
                .toList();

        for (ItemGroup itemGroup : groupsToMove) {
            boolean spotFound = false;
            for (int page = maxPages - 2; page >= 0; page--) { // Scan all pages from highest to lowest for free spots  |  -2 because if e.g. maxPages is 3, we want to scan pages 1 and 2 but we also have to account for 0-indexing so we actually need to scan 0 and 1
                for (int row = 0; row <= 1; row++) { // Scan both rows for free spots
                    for (int col = 0; col <= 4; col++) { // Scan all columns on that row for free spots; one row has 5 columns (0-4)
                        final int finalPage = page;
                        final int finalRow = row;
                        final int finalCol = col;

                        boolean spotTaken = ItemGroups.getGroups().stream().anyMatch(igroup ->
                                ((ItemGroupAccessor) igroup).tabmanager$getPage() == finalPage &&
                                        igroup.getRow().ordinal() == finalRow &&
                                        igroup.getColumn() == finalCol
                        );

                        if (!spotTaken) {
                            // Spot is free, move the item group here
                            ((ItemGroupAccessor) itemGroup).tabmanager$setPage(finalPage);
                            ((ItemGroupAccessor) itemGroup).tabmanager$setRow(row == 1 ? ItemGroup.Row.BOTTOM : ItemGroup.Row.TOP); // Set row
                            ((ItemGroupAccessor) itemGroup).tabmanager$setColumn(finalCol); // Set column
                            spotFound = true;
                            break; // Break out of column loop
                        }
                    }

                    if (spotFound) break; // Break out of row loop
                }

                if (spotFound) break; // Break out of page loop
            }
        }

        Config.INSTANCE.fakePages--;
        if (currentPage > maxPages) {
            currentPage = maxPages;
        }

        updatePageCount();
        updateItemGroups();
    }

    /**
     * Changes the column of the selected tab
     * @param toRight True to move right, false to move left
     */
    private void changeColumn(boolean toRight) {
        TabWidget selectedTab = getSelectedTab();
        if (selectedTab == null) return; // No tab selected, do nothing

        if (toRight && selectedTab.reference.getColumn() >= ITEM_GROUPS_PER_ROW - 1) return; // Cannot move right anymore
        if (!toRight && selectedTab.reference.getColumn() <= 0) return; // Cannot move left anymore

        int currentColumn = selectedTab.reference.getColumn();
        int targetColumn = toRight ? currentColumn + 1 : currentColumn - 1;

        // Move other tab if necessary
        ItemGroups.getGroupsToDisplay()
                .stream()
                .filter(o -> ((ItemGroupAccessor) o).tabmanager$getPage() == ((ItemGroupAccessor) selectedTab.reference).tabmanager$getPage() &&
                        o.getRow() == selectedTab.reference.getRow() &&
                        o.getColumn() == targetColumn)
                .forEach(o -> ((ItemGroupAccessor) o).tabmanager$setColumn(currentColumn)); // SHOULD only be one tab here

        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setColumn(targetColumn);
        updateItemGroups();
        updateButtons();
    }
}
