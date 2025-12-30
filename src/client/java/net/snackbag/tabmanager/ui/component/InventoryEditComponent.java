package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.snackbag.tabmanager.TabManagerClient;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.util.CreativeMenuUtility;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class InventoryEditComponent {

    private static final int ITEM_GROUPS_PER_ROW = 5;

    protected final GridLayout topItemGroupRow, bottomItemGroupRow;
    protected final GridLayout tabControlGridContainer, tabControlGrid;
    protected final ScrollContainer<FlowLayout> trayScrollContainer;
    protected final FlowLayout trayLayout;
    protected final StackLayout inventoryLayout;
    protected final FlowLayout inventoryContainerLayout;
    protected final FlowLayout componentLayout;

    protected List<TabWidget> tabs = new ArrayList<>();
    
    protected final TextureComponent creativeInventoryTexture;
    protected final ButtonComponent editFilterButton, nextPageButton, previousPageButton;
    protected final ButtonComponent pageLabel; // Button Component as Label because it's easier to work with and look the same

    protected IconButtonComponent moveLeftButton, moveRightButton, moveUpButton, moveDownButton, toTrayButton, fromTrayButton, newPageButton, removePageButton, changeIconButton;
    protected BiConsumer<ButtonComponent, TabWidget> onIconChangeClick;

    protected final int textureWidth, textureHeight;
    protected final int fixedButtonWidth, fixedButtonHeight;
    protected final int pageSwitchWidth, pageSwitchHeight;
    
    public static final float FILTER_BUTTON_MULTIPLIER_W = 162/195f; // The original width multiplier of the image to calculate button width
    public static final float FILTER_BUTTON_MULTIPLIER_H = 18/127f;  // The original height multiplier of the image to calculate button height

    public static final float PAGE_SWITCH_BUTTON_MULTIPLIER_W = 54/195f;
    public static final float PAGE_SWITCH_BUTTON_MULTIPLIER_H = 18/127f;

    public static final int INVENTORY_BORDER_WIDTH = 8;

    // Magic numbers because positioning is fucking hard and I don't feel like investing brain power when this also does it
    public static final int FILTER_BUTTON_POS_MAGIC_NUMBER_Y = 80;
    public static final int PAGE_SWITCH_BUTTON_POS_MAGIC_NUMBER_Y = 102;

    private static final int TRAY_WIDTH = 162;
    private static final int TRAY_HEIGHT = 72;

    protected int currentPage = 1;
    protected int maxPages = CreativeMenuUtility.getPageCount();
    
    public static final Identifier inventoryTextureIdentifier = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/creative_inventory.png");

    /**
     * Draws the creative inventory with the edit button on in the editor screen
     * @param textureWidth How wide the inventory should be (influences button width)
     * @param textureHeight How high the inventory should be (influences button height)
     * @param onFilterClick what happens if the filterButton is clicked
     * @param onIconChangeClick what happens if the change icon button is clicked
     */
    public InventoryEditComponent(int textureWidth, int textureHeight, BiConsumer<ButtonComponent, @Nullable TabWidget> onFilterClick, BiConsumer<ButtonComponent, @Nullable TabWidget> onIconChangeClick) {
        // Set sizes
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.onIconChangeClick = onIconChangeClick;

        // Calculate button sizes based on texture size
        fixedButtonWidth = (int) (textureWidth * FILTER_BUTTON_MULTIPLIER_W);
        fixedButtonHeight = (int) (textureHeight * FILTER_BUTTON_MULTIPLIER_H);

        pageSwitchWidth = (int) (textureWidth * PAGE_SWITCH_BUTTON_MULTIPLIER_W);
        pageSwitchHeight = (int) (textureHeight * PAGE_SWITCH_BUTTON_MULTIPLIER_H);

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

        trayLayout = Containers.ltrTextFlow(Sizing.fixed(TRAY_WIDTH), Sizing.content());

        trayScrollContainer = Containers.verticalScroll(Sizing.fixed(TRAY_WIDTH), Sizing.fixed(TRAY_HEIGHT), trayLayout);
        trayScrollContainer.positioning(Positioning.absolute(INVENTORY_BORDER_WIDTH, INVENTORY_BORDER_WIDTH)) // It IS passed correctly, IntelliJ!
                .padding(Insets.of(2)); // A lil' bit of padding won't hurt

        componentLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());
        componentLayout.horizontalAlignment(HorizontalAlignment.CENTER);
        componentLayout.gap(10);

        // Initialize components
        this.creativeInventoryTexture = Components.texture(
                inventoryTextureIdentifier,
                0, 0,
                textureWidth, textureHeight,
                textureWidth, textureHeight);
        
        editFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.edit_item_masks"), btn -> onFilterClick.accept(btn, getSelectedTab()));
        editFilterButton.zIndex(10)
                .positioning(Positioning.absolute(INVENTORY_BORDER_WIDTH, FILTER_BUTTON_POS_MAGIC_NUMBER_Y))
                .sizing(Sizing.fixed(fixedButtonWidth), Sizing.fixed(fixedButtonHeight));

        nextPageButton = Components.button(Text.literal("->"), (btn) -> nextPage());
        nextPageButton.zIndex(10)
                .positioning(Positioning.absolute(INVENTORY_BORDER_WIDTH + pageSwitchWidth * 2, PAGE_SWITCH_BUTTON_POS_MAGIC_NUMBER_Y))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight))
                .tooltip(Text.translatable("tabmanager.gui.edit_screen.next_page_tooltip"));

        previousPageButton = Components.button(Text.literal("<-"), (btn) -> previousPage());
        previousPageButton.zIndex(10)
                .positioning(Positioning.absolute(INVENTORY_BORDER_WIDTH, PAGE_SWITCH_BUTTON_POS_MAGIC_NUMBER_Y))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight))
                .tooltip(Text.translatable("tabmanager.gui.edit_screen.previous_page_tooltip"));

        pageLabel = Components.button(Text.translatable("tabmanager.gui.edit_screen.page", currentPage, maxPages), (btn) -> {});
        pageLabel.active(false)
                .zIndex(10)
                .positioning(Positioning.absolute(INVENTORY_BORDER_WIDTH + pageSwitchWidth, PAGE_SWITCH_BUTTON_POS_MAGIC_NUMBER_Y))
                .sizing(Sizing.fixed(pageSwitchWidth), Sizing.fixed(pageSwitchHeight));

        initTabControls();
    }

    /**
     * Builds the component
     */
    public void build(Function<FlowLayout, FlowLayout> addToParent) {
        inventoryLayout.child(creativeInventoryTexture)
                .child(trayScrollContainer)
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

        refresh();

        addToParent.apply(componentLayout);
    }

    private void initTabControls() {
        moveLeftButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_left.png"), 13, 13, (btn) -> changeColumn(false));
        moveRightButton =   new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_right.png"), 13, 13, (btn) -> changeColumn(true));
        moveUpButton =      new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_up.png"), 13, 13, (btn) -> changeRow(false));
        moveDownButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/arrow_down.png"), 13, 13, (btn) -> changeRow(true));
        toTrayButton =      new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/to_tray.png"), 13, 13, (btn) -> moveToTray());
        fromTrayButton =    new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/from_tray.png"), 13, 13, (btn) -> moveFromTray());
        changeIconButton =  new IconButtonComponent(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/change_icon.png"), 13, 13, (btn) -> onIconChangeClick.accept(btn, getSelectedTab()));
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

    /**
     * Refreshes the entire component
     */
    public void refresh() {
        updatePageCount();
        initializeTabs();
        updateItemGroups();
        updateButtons();
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
        updateButtons();
    }

    private void previousPage() {
        if (currentPage <= 1) return; // Cannot go less than page 1

        currentPage--;
        updatePageLabel();
        updateItemGroups();
        updateButtons();
    }

    private void initializeTabs() {
        tabs.clear();
        for (ItemGroup itemGroup : ItemGroups.getGroups()) {
            TabWidget tab = getTab(itemGroup);
            tab.setInTray(((ItemGroupAccessor) itemGroup).tabmanager$isHidden());
            tab.build(); // Build it once
            tabs.add(tab);
        }
    }

    private void updatePageLabel() {
        pageLabel.setMessage(Text.translatable("tabmanager.gui.edit_screen.page", currentPage, maxPages));
    }

    private void updateItemGroups() {
        List<ItemGroup> groups = CreativeMenuUtility.getItemGroupsOnPage(currentPage - 1, true); // Pages are 0-indexed internally
        clearComponent(topItemGroupRow); // Clear the rows
        clearComponent(bottomItemGroupRow);
        clearComponent(trayLayout);

        // Now add them back
        for (TabWidget tab : tabs) {
            if (tab.reference == null) continue; // Just in case
            if (!tab.isInTray() && !groups.contains(tab.reference)) continue; // Not on this page

            tab.updateIcon();

            if (tab.isInTray()) {
                trayLayout.child(tab.build());
                continue;
            }

            if (tab.reference.getRow() == ItemGroup.Row.TOP)
                topItemGroupRow.child(tab.build(), 0, tab.reference.getColumn());
            else
                bottomItemGroupRow.child(tab.build(), 0, tab.reference.getColumn());
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
        } // No tab selected, do nothing

        if (selectedTab.isInTray()) {
            moveLeftButton.setActive(false);
            moveRightButton.setActive(false);
            moveUpButton.setActive(false);
            moveDownButton.setActive(false);
            toTrayButton.setActive(false);
            fromTrayButton.setActive(getFreeSpotInPage(currentPage - 1) != null); // Only active if there is a free spot
            return;
        }

        moveLeftButton.setActive(selectedTab.reference.getColumn() != 0);
        moveRightButton.setActive(selectedTab.reference.getColumn() != ITEM_GROUPS_PER_ROW - 1);
        moveUpButton.setActive(selectedTab.reference.getRow() != ItemGroup.Row.TOP);
        moveDownButton.setActive(selectedTab.reference.getRow() != ItemGroup.Row.BOTTOM);
        toTrayButton.setActive(!selectedTab.isInTray());
        fromTrayButton.setActive(selectedTab.isInTray() && getFreeSpotInPage(currentPage - 1) != null); // Only active if there is a free spot
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
            for (int page = maxPages - 2; page >= 0; page--) { // Scan all pages from highest to lowest for free spots  |  -2 because if e.g. maxPages is 3, we want to scan pages 1 and 2 but we also have to account for 0-indexing so we actually need to scan 0 and 1
                Pair<ItemGroup.Row, Integer> freeSpot = getFreeSpotInPage(page);

                if (freeSpot == null) continue; // No free spot on this page, try next one

                // Spot is free, move the item group here
                ((ItemGroupAccessor) itemGroup).tabmanager$setPage(page);
                ((ItemGroupAccessor) itemGroup).tabmanager$setRow(freeSpot.getLeft()); // Set row
                ((ItemGroupAccessor) itemGroup).tabmanager$setColumn(freeSpot.getRight()); // Set column
                break; // Move to next item group
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
        if (selectedTab.isInTray()) return; // Tab is in tray, cannot move

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

    private void changeRow(boolean toBottom) {
        TabWidget selectedTab = getSelectedTab();
        if (selectedTab == null) return; // No tab selected, do nothing
        if (selectedTab.isInTray()) return; // Tab is in tray, cannot move

        if (toBottom && selectedTab.reference.getRow() == ItemGroup.Row.BOTTOM) return; // Cannot move down anymore
        if (!toBottom && selectedTab.reference.getRow() == ItemGroup.Row.TOP) return; // Cannot move up anymore

        ItemGroup.Row targetRow = toBottom ? ItemGroup.Row.BOTTOM : ItemGroup.Row.TOP;

        // Move other tab if necessary
        ItemGroups.getGroupsToDisplay()
                .stream()
                .filter(o -> ((ItemGroupAccessor) o).tabmanager$getPage() == ((ItemGroupAccessor) selectedTab.reference).tabmanager$getPage() &&
                        o.getRow() == targetRow &&
                        o.getColumn() == selectedTab.reference.getColumn())
                .forEach(o -> ((ItemGroupAccessor) o).tabmanager$setRow(selectedTab.reference.getRow())); // SHOULD only be one tab here

        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setRow(targetRow);
        updateItemGroups();
        updateButtons();
    }

    private void moveToTray() {
        TabWidget selectedTab = getSelectedTab();
        if (selectedTab == null) return; // No tab selected, do nothing
        if (selectedTab.isInTray()) return; // Already in tray

        selectedTab.setInTray(true);
        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setHidden(true);
        updateItemGroups();
        updateButtons();
    }

    private void moveFromTray() {
        TabWidget selectedTab = getSelectedTab();
        if (selectedTab == null) return; // No tab selected, do nothing
        if (!selectedTab.isInTray()) return; // Not in tray

        Pair<ItemGroup.Row, Integer> freeSpot = getFreeSpotInPage(currentPage - 1);
        if (freeSpot == null) return; // No free spot, cannot move

        selectedTab.setInTray(false);

        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setPage(currentPage - 1);
        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setRow(freeSpot.getLeft());
        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setColumn(freeSpot.getRight());
        ((ItemGroupAccessor) selectedTab.reference).tabmanager$setHidden(false);

        updateItemGroups();
        updateButtons();
    }

    // HELPER FUNCTIONS ------------------------------------------------
    // -----------------------------------------------------------------

    /**
     * Checks if there is any free spot in the specified page
     * @param page The page 0-indexed to check
     * @return Pair non-null if a free spot was found, null otherwise
     */
    private @Nullable Pair<ItemGroup.Row, Integer> getFreeSpotInPage(int page) {
        for (int row = 0; row <= 1; row++) {
            Integer freeCol = getFreeColumnInRow(row == 0 ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM, page);
            if (freeCol != null) return new Pair<>(row == 0 ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM, freeCol);
        }

        return null;
    }

    /**
     * Returns a free column in the specified row and page, or null if none found
     * @param row The row to check
     * @param page The page 0-indexed to check
     * @return The free column index, or null if none found
     */
    private @Nullable Integer getFreeColumnInRow(ItemGroup.Row row, int page) {
        for (int col = 0; col < ITEM_GROUPS_PER_ROW; col++) {
            final int finalCol = col;
            boolean spotTaken = ItemGroups.getGroups().stream().anyMatch(igroup ->
                    ((ItemGroupAccessor) igroup).tabmanager$getPage() == page &&
                            igroup.getRow() == row &&
                            igroup.getColumn() == finalCol &&
                            !((ItemGroupAccessor) igroup).tabmanager$isHidden()
            );

            if (!spotTaken) {
                return col;
            }
        }

        return null;
    }
}
