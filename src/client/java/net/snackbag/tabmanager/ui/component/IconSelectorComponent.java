package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class IconSelectorComponent extends OverlayContainer<FlowLayout> {

    protected static final List<Item> items = Registries.ITEM.getEntrySet().stream().map(Map.Entry::getValue).toList();
    protected List<Item> display = new CopyOnWriteArrayList<>(items);

    protected List<ItemSelectableComponent> selectables = new ArrayList<>();

    protected FlowLayout componentLayout;
    protected FlowLayout tabLayout;
    protected FlowLayout itemResultLayout;

    protected ScrollContainer<FlowLayout> itemResultScroll;

    protected ButtonComponent closeButton;
    protected ButtonComponent searchButton;

    protected TextBoxComponent searchBar;

    protected LabelComponent itemGroupNameLabel;

    protected Consumer<IconSelectorComponent> hide;
    protected Consumer<IconSelectorComponent> show;
    protected Consumer<IconSelectorComponent> close;

    protected @Nullable TabWidget tabWidget = null;
    protected @Nullable Runnable onChange = null;

    public IconSelectorComponent(Consumer<IconSelectorComponent> hide, Consumer<IconSelectorComponent> show, Consumer<IconSelectorComponent> close) {
        this(Containers.verticalFlow(Sizing.fixed(400), Sizing.content()));
        this.hide = hide;
        this.show = show;
        this.close = close;
    }

    private IconSelectorComponent(FlowLayout layout) {
        super(layout);

        this.componentLayout = layout;
        componentLayout.surface(Surface.DARK_PANEL);
        componentLayout.padding(Insets.of(5));
        componentLayout.positioning(Positioning.absolute(0, 0))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        this.tabLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        tabLayout.padding(Insets.of(5))
                .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);

        this.itemGroupNameLabel = Components.label(Text.empty());
        itemGroupNameLabel.verticalTextAlignment(VerticalAlignment.CENTER)
                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                .sizing(Sizing.content(), Sizing.content())
                .margins(Insets.left(5));

        this.itemResultLayout = Containers.ltrTextFlow(Sizing.fill(100), Sizing.content());
        itemResultLayout.padding(Insets.of(3));

        this.itemResultScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(150), itemResultLayout);
        itemResultScroll.surface(Surface.PANEL_INSET);

        this.searchBar = Components.textBox(Sizing.fill(100));

        this.closeButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.close_button"), btn -> close());
        closeButton.sizing(Sizing.fill(100), Sizing.content())
                .margins(Insets.top(5));

        this.searchButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.icon.search_item"), btn -> this.search());
        searchButton.sizing(Sizing.fill(100), Sizing.content())
                .margins(Insets.bottom(5));

        assemble();
    }

    private void assemble() {
        updateItemList();

        componentLayout.child(tabLayout)
                .child(searchBar)
                .child(searchButton)
                .child(itemResultScroll)
                .child(closeButton);
    }

    /**
     * Creates an ItemSelectableComponent for the given item
     * @param item The item to create the selectable for
     * @return The created ItemSelectableComponent
     */
    private ItemSelectableComponent getItemSelectable(Item item) {
        ItemSelectableComponent itemSelectable = new ItemSelectableComponent(
                Sizing.fixed(20), Sizing.fixed(20),
                item,
                selectable -> {
                    selectables.stream().filter(s -> s != selectable).forEach(s -> s.setActive(false));
                    selectable.toggleActive();

                    if (tabWidget != null) {
                        ((ItemGroupAccessor) tabWidget.reference).tabmanager$setIcon(item.getDefaultStack());
                        updateWidget();
                        if (onChange != null) onChange.run();
                    }
                });

        selectables.add(itemSelectable);
        return itemSelectable;
    }

    /**
     * Updates the item list display based on the current display list
     */
    private void updateItemList() {
        this.itemResultLayout.clearChildren();
        this.selectables.clear();
        display.forEach(item -> itemResultLayout.child(getItemSelectable(item).assemble()));
    }

    /**
     * Updates the tab widget icon to reflect the current icon selection if tabWidget is not null
     */
    private void updateWidget() {
        if (tabWidget != null) tabWidget.updateIcon();
    }

    /**
     * Sets the tab widget to be edited by this component
     * @param widget The tab widget
     * @param onChange A runnable to be called when the icon is changed (can be null)
     */
    public void setTabWidget(@NotNull TabWidget widget, @Nullable Runnable onChange) {
        this.onChange = onChange;
        this.tabWidget = widget.clone();
        tabLayout.child(tabWidget
                .setOnPress(tab -> {} /* do nothing */)
                .setPressable(false /* do not press, just display */)
                .build());

        tabWidget.setActive(false); // Must be handled separately here (see method comment!)

        itemGroupNameLabel.text(widget.reference.getDisplayName());
        tabLayout.child(itemGroupNameLabel);
    }

    public void clearTabWidget() {
        this.tabLayout.clearChildren();
        this.tabWidget = null;
    }

    public void hide() {
        this.hide.accept(this);
    }

    public void show() {
        this.show.accept(this);
    }

    public void close() {
        hide();

        searchBar.text(""); // Clear search entry
        selectables.forEach(selectable -> selectable.setActive(false));
        display = new CopyOnWriteArrayList<>(items);
        updateItemList();

        this.close.accept(this);
    }

    /**
     * Searches for items based on the current search bar text and updates the display list accordingly
     */
    private void search() {
        String term = searchBar.getText();
        if (term.isEmpty()) {
            display = new CopyOnWriteArrayList<>(items);
        } else {
            ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (clientPlayNetworkHandler != null) {
                display.clear();
                //? if =1.21.1
                //SearchManager searchManager = clientPlayNetworkHandler.getSearchManager();
                SearchProvider<ItemStack> searchProvider = /*? if =1.21.1 {*/
                        /*searchManager.getItemTooltipReloadFuture();*/
                        /*?} elif =1.20.1 {*/
                        MinecraftClient.getInstance().getSearchProvider(SearchManager.ITEM_TOOLTIP);
                        /*?}*/

                display.addAll(searchProvider.findAll(term.toLowerCase(Locale.ROOT)).stream().map(ItemStack::getItem).toList());
            }
        }

        updateItemList();
    }
}