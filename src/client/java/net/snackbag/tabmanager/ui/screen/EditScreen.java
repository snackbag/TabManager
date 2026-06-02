package net.snackbag.tabmanager.ui.screen;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.TabManagerClient;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.file_dialog.NativeFileDialogs;
import net.snackbag.tabmanager.filesystem.ConfigDirectory;
import net.snackbag.tabmanager.ui.component.FilterEditComponent;
import net.snackbag.tabmanager.ui.component.FilterListComponent;
import net.snackbag.tabmanager.ui.component.IconSelectorComponent;
import net.snackbag.tabmanager.ui.component.InventoryEditComponent;
import net.snackbag.tabmanager.ui.toast.ZToast;
import net.snackbag.tabmanager.util.ItemFilter;
import net.snackbag.tabmanager.util.ItemGroupUtility;
import net.snackbag.tabmanager.util.ToastUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditScreen extends BaseOwoScreen<FlowLayout> {

    protected InventoryEditComponent inventoryEditComponent;
    protected FilterEditComponent filterEditComponent;
    protected FilterListComponent filterListComponent;

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

        FlowLayout stageLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));

        FlowLayout controlPanel = Containers.verticalFlow(Sizing.fixed(controlPanelWidth), Sizing.content());
        FlowLayout canvasPanel = Containers.horizontalFlow(Sizing.expand(), Sizing.fill(100));

        IconSelectorComponent iconSelectorComponent = new IconSelectorComponent(
                Component::remove, // on hide
                rootComponent::child, // on show
                IconSelectorComponent::clearTabWidget); // on close
        iconSelectorComponent.zIndex(1000);

        // References to allow mutual access between components
        filterEditComponent = new FilterEditComponent(
                Component::remove, // on hide
                rootComponent::child, // on show
                this::onFilterSave // on save
        );
        filterEditComponent.zIndex(2000);

        filterListComponent = new FilterListComponent(
                Component::remove, // on hide
                rootComponent::child,  // on show
                (filter, isNew) -> { // on edit and add
                    filterEditComponent.initialize(filter, isNew);
                    filterEditComponent.show();
                }
        );
        filterListComponent.zIndex(1000);

        inventoryEditComponent = new InventoryEditComponent(
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

        ButtonComponent loadConfigButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.import_config_button"), (btn) -> importConfig());
        ButtonComponent saveConfigButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.export_config_button"), (btn) -> exportConfig());
        ButtonComponent newConfigButton  = Components.button(Text.translatable("tabmanager.gui.edit_screen.new_config_button"), (btn) -> newConfig());

        confCtrlContainer
                .child(loadConfigButton)
                .child(saveConfigButton)
                .child(newConfigButton);

        confCtrlContainer.forEachDescendant(c -> c.sizing(Sizing.fill(100), Sizing.content()));

        rootComponent.child(confCtrlContainer);
    }

    private void newConfig() {
        try {
            ConfigDirectory.backupConfigFile();
            Config.loadConfig(new Config());
            Config.reload();
            ItemGroupUtility.resetItemGroups();
            inventoryEditComponent.refresh();
        } catch (IOException e) {
            TabManagerClient.LOGGER.error("Failed to create new config", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Uses TinyFD to export the current config to a chosen location in a separate thread
     */
    private void exportConfig() {
        MinecraftClient.getInstance().execute(() -> {
            AtomicBoolean aborted = new AtomicBoolean(false);
            String savePath = NativeFileDialogs.save(
                    Text.translatable("tabmanager.gui.edit_screen.export_config_button").getString(),
                    new NativeFileDialogs.FilterItem(
                        "Tab Manager Config Files",
                        new String[] {"*.json", "*.tmconfig"}),
                    ConfigDirectory.getConfigDirectory().toAbsolutePath().toString(),
                    "config.tmconfig",
                    (msg) -> aborted.set(true));

            if (aborted.get()) return;

            try {
                Config.writeConfigFile(new File(savePath)); // Cannot be null here because of the aborted check
            } catch (IOException e) {
                TabManagerClient.LOGGER.error("Failed to export config to {}", savePath, e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Uses TinyFD to import a config from a chosen location in a separate thread
     */
    private void importConfig() {
        MinecraftClient.getInstance().execute(() -> {
            AtomicBoolean aborted = new AtomicBoolean(false);
            String loadPath = NativeFileDialogs.open(
                    Text.translatable("tabmanager.gui.edit_screen.import_config_button").getString(),
                    new NativeFileDialogs.FilterItem(
                        "Tab Manager Config Files",
                        new String[] {"*.json", "*.tmconfig"}),
                    ConfigDirectory.getConfigDirectory().toAbsolutePath().toString(),
                    (msg) -> aborted.set(true));

            if (aborted.get()) return;

            try {
                Config.loadConfigFile(new File(loadPath)); // Cannot be null here because of the aborted check
            } catch (IOException e) {
                TabManagerClient.LOGGER.error("Failed to import config from {}", loadPath, e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Called when a filter is saved from the FilterEditComponent
     */
    private void onFilterSave(FilterEditComponent comp) {
        if (comp.isNew()) { // IF is new, add to config and update
            ItemFilter filter = comp.getFilter();

            if (filter != null) {
                Config.INSTANCE.filters.add(comp.getFilter());
                Config.reload();
            } else {

                // Show system toast on error
                ToastUtil.displayToast(
                        MinecraftClient.getInstance(),
                        //? if =1.21.1
                        //ZToast.ZToastType.WARNING,
                        Text.translatable("tabmanager.gui.edit_screen.filter.toast.error.invalid_predicate_title"),
                        Text.translatable("tabmanager.gui.edit_screen.filter.toast.error.invalid_predicate_description"),
                        500f // Very high Z to ensure visibility
                );

                TabManagerClient.LOGGER.error("Failed to compile ItemFilter");
            }
        } else { // ... otherwise just update
            Config.reload();
        }

        comp.close();

        filterListComponent.refresh(); // Refresh the filter list to show changes
    }

    @Override
    public void close() {
        super.close();

        // Save config on screen exit
        try {
            ItemGroupUtility.saveItemGroupsToConfig();

            ConfigDirectory.backupConfigFile();
            Config.writeConfigFile(ConfigDirectory.getConfigFile());

            // Refresh inventory to apply any changes made
            inventoryEditComponent.refresh();
            Config.reload();
            ItemGroupUtility.reloadItemGroups();
        } catch (IOException e) {
            TabManagerClient.LOGGER.error("Failed to save config on exit", e);
            throw new RuntimeException(e);
        }
    }
}