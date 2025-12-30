package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ItemSelectableComponent {

    private boolean isActive;

    protected final StackLayout componentLayout;
    protected final ItemComponent itemComponent;
    protected final InvisibleButtonComponent button;

    public ItemSelectableComponent(Sizing horizontal, Sizing vertical, Item item, Consumer<ItemSelectableComponent> onPress) {
        componentLayout = Containers.stack(horizontal, vertical);
        componentLayout.padding(Insets.of(3));

        itemComponent = Components.item(item.getDefaultStack());
        itemComponent.sizing(Sizing.fill(), Sizing.fill());

        button = new InvisibleButtonComponent(Text.empty(), btn -> onPress.accept(this));
        button.sizing(horizontal, vertical)
                .zIndex(20);
    }

    public StackLayout assemble() {
        return componentLayout.child(itemComponent)
                .child(button);
    }

    public ItemSelectableComponent toggleActive() {
        return setActive(!isActive);
    }

    public ItemSelectableComponent setActive(boolean active) {
        isActive = active;
        componentLayout.surface(active ? Surface.PANEL : Surface.BLANK);
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

}
