package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;

import java.util.function.Consumer;

public class TabWidget {

    private boolean active;
    private ItemStack icon;

    protected TextureComponent tabTexture;
    private StackLayout layout;

    protected Consumer<TabWidget> onPress;

    protected final ItemGroup reference;

    public static final Identifier TAB_TEXTURE_IDENTIFIER = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/tab.png");
    public static final Identifier TAB_TEXTURE_IDENTIFIER_ACTIVE = Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/image/tab_active.png");

    public TabWidget(ItemGroup reference, boolean isActive, Consumer<TabWidget> onPress) {
        this.reference = reference;
        this.active = isActive;
        this.onPress = onPress;
        this.icon = reference.getIcon();

        tabTexture = Components.texture(isActive ? TAB_TEXTURE_IDENTIFIER_ACTIVE : TAB_TEXTURE_IDENTIFIER, 0, 0, 26, 26, 26, 26);
    }

    public StackLayout build() {
        layout = Containers.stack(Sizing.content(), Sizing.content());

        InvisibleButtonComponent button = new InvisibleButtonComponent(Text.empty(), buttonComponent -> {
            toggleActive();
            onPress.accept(this);
        });

        button.sizing(Sizing.fixed(26), Sizing.fixed(26))
                .positioning(Positioning.relative(0, 0))
                .zIndex(20);

        ItemComponent item = Components.item(icon);
        item.zIndex(10)
                .positioning(Positioning.relative(50, 50));

        layout.child(tabTexture).child(item).child(button);

        return layout;
    }

    public boolean isActive() {
        return active;
    }

    public TabWidget setActive(boolean isActive) {
        this.active = isActive;

        // Remove texture, update texture, add texture
        this.layout.removeChild(this.tabTexture);
        tabTexture = Components.texture(isActive ? TAB_TEXTURE_IDENTIFIER_ACTIVE : TAB_TEXTURE_IDENTIFIER, 0, 0, 26, 26, 26, 26);
        tabTexture.zIndex(0);
        this.layout.child(this.tabTexture);
        return this;
    }

    public TabWidget toggleActive() {
        setActive(!active);
        return this;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public TabWidget setIcon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

}
