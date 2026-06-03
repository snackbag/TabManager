package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.util.ItemFilter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FilterListComponent extends OverlayContainer<FlowLayout> {

    protected List<ItemFilter> filters;

    protected Consumer<FilterListComponent> hide;
    protected Consumer<FilterListComponent> show;
    protected BiConsumer<@Nullable ItemFilter, Boolean> onEditAndAdd;

    protected FlowLayout componentLayout;

    protected ScrollContainer<FlowLayout> filterScroll;
    protected FlowLayout filterLayout;

    protected FlowLayout controlLayout;

    protected ButtonComponent addFilterButton;
    protected ButtonComponent removeFilterButton;
    protected ButtonComponent editFilterButton;

    protected ButtonComponent closeButton;

    protected List<ClickableFlowComponent> filterComponents;

    public FilterListComponent(Consumer<FilterListComponent> hide, Consumer<FilterListComponent> show, BiConsumer<@Nullable ItemFilter, Boolean> onEditAndAdd) {
        this(Containers.verticalFlow(Sizing.fixed(350), Sizing.content()));
        this.hide = hide;
        this.show = show;
        this.onEditAndAdd = onEditAndAdd;
    }

    protected FilterListComponent(FlowLayout child) {
        super(child);

        this.componentLayout = child;
        componentLayout.surface(Surface.DARK_PANEL)
                .padding(Insets.of(5));

        this.filterLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        filterLayout.gap(2);

        this.filterScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(150), filterLayout);
        filterScroll.surface(Surface.PANEL_INSET)
                .padding(Insets.of(2));

        this.controlLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

        addFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.filter.add_filter"), button -> onEditAndAdd.accept(null, true));
        addFilterButton.sizing(Sizing.fill(33), Sizing.content());

        editFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.filter.edit_filter"), button -> {
            ItemFilter selected = getSelectedFilter();
            if (selected != null)
                onEditAndAdd.accept(selected, false);
        });

        editFilterButton.sizing(
                /*? if >=1.20.3 {*/
                Sizing.expand()
                 /*?} else {*/
                /*Sizing.fixed(100)
                *//*?}*/,
                Sizing.content()
        );

        removeFilterButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.filter.remove_filter"), button -> {
            ItemFilter selected = getSelectedFilter();
            if (selected != null) {
                Config.INSTANCE.filters.remove(selected);
                Config.reload();
                refresh();
            }
        });

        removeFilterButton.sizing(Sizing.fill(33), Sizing.content());

        closeButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.close_button"), button -> close());
        closeButton.sizing(Sizing.fill(100), Sizing.content());

        updateFilters();
        updateButtonStates();
        assemble();
    }

    private void assemble() {
        this.controlLayout.child(addFilterButton)
                .child(removeFilterButton)
                .child(editFilterButton);

        this.componentLayout.child(filterScroll)
                .child(controlLayout)
                .child(closeButton);
    }

    private ClickableFlowComponent createFilterComponent(ItemFilter filter) {
        ClickableFlowComponent filterComponent = new ClickableFlowComponent(Sizing.fill(100), Sizing.content(), FlowLayout.Algorithm.HORIZONTAL, btn -> {
            btn.toggleActive();
            filterComponents.stream().filter(c -> c != btn).forEach(c -> c.setActive(false));
            updateButtonStates();
        });

        filterComponent.child(Components.label(Text.literal(filter.getPredicateSource())))
                .padding(Insets.of(3));

        filterComponents.add(filterComponent);

        return filterComponent;
    }

    private @Nullable ItemFilter getSelectedFilter() {
        return filterComponents.stream()
                .filter(ClickableFlowComponent::isActive)
                .findFirst()
                .map(fc -> filters.get(filterComponents.indexOf(fc)))
                .orElse(null);
    }

    private void updateButtonStates() {
        boolean anySelected = filterComponents.stream().anyMatch(ClickableFlowComponent::isActive);
        removeFilterButton.active(anySelected);
        editFilterButton.active(anySelected);
    }

    private void updateFilters() {
        this.filters = Config.INSTANCE.filters;
        this.filterLayout.clearChildren();
        this.filterComponents = new java.util.ArrayList<>();
        this.filters.forEach(filter -> this.filterLayout.child(createFilterComponent(filter)));
    }

    public void show() {
        refresh();
        this.show.accept(this);
    }

    public void hide() {
        this.hide.accept(this);
    }

    public void close() {
        hide();

        filterComponents.forEach(fc -> fc.setActive(false));
        updateButtonStates();
        filterComponents.clear();
    }

    public void refresh() {
        updateFilters();
        updateButtonStates();
    }
}
