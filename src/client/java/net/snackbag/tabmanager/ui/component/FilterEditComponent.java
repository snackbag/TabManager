package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.util.ItemFilter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FilterEditComponent extends OverlayContainer<FlowLayout> {

    public static final List<ItemGroup> itemGroups = ItemGroups.getGroups().stream().filter(group -> !group.isSpecial()).toList();

    protected ItemFilter filter;
    protected boolean isNew;

    protected Consumer<FilterEditComponent> hide, show, save;

    protected FlowLayout componentLayout;
    protected FlowLayout groupSelectionLayout;

    protected GridLayout groupLabelsLayout;

    protected ScrollContainer<FlowLayout> availableGroupsScroll;
    protected FlowLayout availableGroupsLayout;
    protected List<ItemGroup> availableGroups = new ArrayList<>(); // Groups that are not applied

    protected ScrollContainer<FlowLayout> appliedGroupsScroll;
    protected FlowLayout appliedGroupsLayout;
    protected List<ItemGroup> appliedGroups = new ArrayList<>();

    protected CheckboxComponent isRegexCheckbox;

    protected TextBoxComponent predicateInput;

    protected ButtonComponent saveButton;
    protected ButtonComponent discardButton;

    protected LabelComponent availableGroupsLabel;
    protected LabelComponent appliedGroupsLabel;

    public FilterEditComponent(Consumer<FilterEditComponent> hide, Consumer<FilterEditComponent> show, Consumer<FilterEditComponent> save) {
        this(Containers.verticalFlow(Sizing.fixed(350), Sizing.content()));
        this.hide = hide;
        this.show = show;
        this.save = save;
    }

    protected FilterEditComponent(FlowLayout child) {
        super(child);

        this.componentLayout = Containers.verticalFlow(Sizing.fixed(350), Sizing.content());
        componentLayout.gap(5);
        componentLayout.surface(Surface.DARK_PANEL)
                .padding(Insets.of(5));

        this.groupSelectionLayout = Containers.horizontalFlow(Sizing.fill(), Sizing.content());

        this.groupLabelsLayout = Containers.grid(Sizing.fill(), Sizing.content(), 1, 2);

        this.availableGroupsLayout = Containers.ltrTextFlow(Sizing.fill(), Sizing.fill());

        this.availableGroupsScroll = Containers.verticalScroll(Sizing.fill(50), Sizing.fixed(150), availableGroupsLayout);
        availableGroupsScroll.padding(Insets.of(2))
            .surface(Surface.PANEL_INSET);

        this.appliedGroupsLayout = Containers.ltrTextFlow(Sizing.fill(), Sizing.fill());

        this.appliedGroupsScroll = Containers.verticalScroll(Sizing.fill(50), Sizing.fixed(150), appliedGroupsLayout);
        appliedGroupsScroll.padding(Insets.of(2))
            .surface(Surface.PANEL_INSET);

        this.isRegexCheckbox = Components.checkbox(Text.translatable("tabmanager.gui.edit_screen.filter.use_regex"));

        this.predicateInput = Components.textBox(Sizing.fill());

        this.saveButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.save_button"), button -> save.accept(this));
        saveButton.sizing(Sizing.fill(), Sizing.content());

        this.discardButton = Components.button(Text.translatable("tabmanager.gui.edit_screen.discard_button"), button -> close());
        discardButton.sizing(Sizing.fill(), Sizing.content());

        this.availableGroupsLabel = Components.label(Text.translatable("tabmanager.gui.edit_screen.filter.available_groups"));
        this.appliedGroupsLabel = Components.label(Text.translatable("tabmanager.gui.edit_screen.filter.applied_groups"));

        updateGroups();
        assemble();
    }

    private void assemble() {
        this.groupLabelsLayout.child(availableGroupsLabel, 0, 0)
            .child(appliedGroupsLabel,0, 1);

        this.groupSelectionLayout.child(availableGroupsScroll)
            .child(appliedGroupsScroll);

        this.componentLayout.child(predicateInput)
                .child(isRegexCheckbox)
                .child(groupLabelsLayout)
                .child(groupSelectionLayout)
                .child(saveButton)
                .child(discardButton);

        this.child(componentLayout);
    }

    public void initialize(@Nullable ItemFilter filter, boolean isNew) {
        this.filter = filter;
        this.isNew = isNew;

        resetComponents();

        if (!isNew && filter != null) {
            String source = filter.getPredicateSource();
            predicateInput.setEditable(false);
            predicateInput.write(source.substring(source.indexOf(":") + 1));

            boolean isRegex = source.startsWith("regex:");
            isRegexCheckbox.checked(isRegex);
            isRegexCheckbox.onChanged(checked -> isRegexCheckbox.checked(isRegex)); // Disable changing regex state for existing filters

            appliedGroups = new ArrayList<>(filter.getApplicableGroups());
            availableGroups = new ArrayList<>(itemGroups.stream().filter(g -> !appliedGroups.contains(g)).toList());
            updateGroups();
        }
    }

    private TabWidget getTabWidget(ItemGroup group) {
        return new TabWidget(group, false, tabWidget -> {
            moveGroup(group, !isApplied(group));
            updateGroups();
        });
    }

    private void updateGroups() {
        availableGroups = new ArrayList<>(itemGroups.stream().filter(g -> !appliedGroups.contains(g)).toList());
        appliedGroups = new ArrayList<>(itemGroups.stream().filter(this::isApplied).toList());

        availableGroupsLayout.clearChildren();
        for (ItemGroup group : availableGroups) {
            availableGroupsLayout.child(getTabWidget(group).build());
        }

        appliedGroupsLayout.clearChildren();
        for (ItemGroup group : appliedGroups) {
            appliedGroupsLayout.child(getTabWidget(group).build());
        }
    }

    /**
     * Moves the given group between available and applied lists.
     * @param group The group to move.
     * @param toApplied If true, moves to applied; else to available.
     */
    private void moveGroup(ItemGroup group, boolean toApplied) {
        if (toApplied) {
            availableGroups.removeIf(g -> g == group);
            appliedGroups.add(group);
        } else {
            appliedGroups.removeIf(g -> g == group);
            availableGroups.add(group);
        }

        // Update the filter's applicable groups
        if (filter != null) {
            filter.clearApplicableGroups();
            appliedGroups.forEach(filter::addApplicableGroup);
        }
    }

    /**
     * Checks if the given group is in the applied list.
     * @param group The group to check.
     * @return True if applied, false otherwise.
     */
    private boolean isApplied(ItemGroup group) {
        return appliedGroups.contains(group);
    }

    public void show() {
        this.show.accept(this);
    }

    public void hide() {
        this.hide.accept(this);
    }

    public void close() {
        hide();
        resetComponents();
    }

    public @Nullable ItemFilter getFilter() {
        if (isNew()) {
            // If new, return a new filter with all the traits set
            String predicateSource = (isRegexCheckbox.isChecked() ? "regex:" : "glob:") + predicateInput.getText();
            ItemFilter newFilter = ItemFilter.parse(predicateSource);

            if (newFilter == null) return null; // Parsing failed

            appliedGroups.forEach(newFilter::addApplicableGroup);
            return newFilter;
        } else {
            // Else, just update the existing filter and return it
            return filter;
        }
    }

    public boolean isNew() {
        return isNew;
    }

    public void resetComponents() {
        predicateInput.setText("");
        predicateInput.setEditable(true);
        isRegexCheckbox.checked(false);
        appliedGroups.clear();
        availableGroups = new ArrayList<>(itemGroups);
        updateGroups();
    }

}

