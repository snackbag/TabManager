package net.snackbag.tabmanager.ui.screen;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.ui.component.FilterEditComponent;
import net.snackbag.tabmanager.ui.component.FilterListComponent;
import net.snackbag.tabmanager.ui.component.IconSelectorComponent;
import net.snackbag.tabmanager.ui.component.InventoryEditComponent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

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

        IconSelectorComponent iconSelectorComponent = new IconSelectorComponent(
                Component::remove, // on hide
                rootComponent::child, // on show
                IconSelectorComponent::clearTabWidget); // on close
        iconSelectorComponent.zIndex(1000);

        // References to allow mutual access between components
        AtomicReference<FilterEditComponent> filterEditRef = new AtomicReference<>();
        AtomicReference<FilterListComponent> filterListRef = new AtomicReference<>();

        FilterEditComponent filterEditComponent = new FilterEditComponent(
                Component::remove, // on hide
                rootComponent::child, // on show
                (comp) -> { // on save
                    if (comp.isNew()) { // IF is new, add to config and update
                        Config.INSTANCE.filters.add(comp.getFilter());
                        Config.reload();
                    } else { // ... otherwise just update
                        Config.reload();
                    }

                    comp.close();

                    filterListRef.get().refresh(); // Refresh the filter list to show changes
                }
        );
        filterEditRef.set(filterEditComponent);
        filterEditComponent.zIndex(2000);

        FilterListComponent filterListComponent = new FilterListComponent(
                Component::remove, // on hide
                rootComponent::child,  // on show
                (filter, isNew) -> { // on edit and add
                    filterEditComponent.initialize(filter, isNew);
                    filterEditComponent.show();
                }
        );
        filterListRef.set(filterListComponent);

        filterListComponent.zIndex(1000);
        InventoryEditComponent inventoryEditComponent = new InventoryEditComponent(
                195, 127,
                (btn, tab) -> { // Item Filter Click
                    filterListComponent.show();
                },
                (btn, tab) -> { // Icon Change Click
                    if (tab == null) return;
                    iconSelectorComponent.setTabWidget(tab, tab::updateIcon);
                    iconSelectorComponent.show();
                });

        controlPanel.surface(Surface.DARK_PANEL);
        controlPanel.margins(Insets.of(5));

        canvasPanel.verticalAlignment(VerticalAlignment.CENTER);
        canvasPanel.horizontalAlignment(HorizontalAlignment.CENTER);

        drawConfigControls(controlPanel, controlPanelWidth);

        inventoryEditComponent.build(canvasPanel::child);

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
}