package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Composite of different components from the OwoLib to create a button with an Icon in the middle
 */
public class IconButtonComponent {

    @Nullable protected Sizing horizontalSizing = null;
    @Nullable protected Sizing verticalSizing = null;

    @Nullable protected Positioning positioning = null;

    protected StackLayout componentLayout;
    protected ButtonComponent button;
    protected TextureComponent textureComponent;

    protected Identifier texture;
    protected final int textureWidth, textureHeight;

    protected final Consumer<ButtonComponent> onPress;

    public IconButtonComponent(Identifier texture, int textureWidth, int textureHeight, Consumer<ButtonComponent> onPress) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.texture = texture;
        this.onPress = onPress;
    }

    /**
     * Builds the component
     * @return StackLayout that represents the IconButtonComponent
     */
    public StackLayout build() {
        textureComponent = Components.texture(texture, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        button = Components.button(Text.empty(), onPress);
        componentLayout = Containers.stack(getHorizontalSizing(), getVerticalSizing());

        button.sizing(Sizing.fixed(textureHeight + 10), Sizing.fixed(textureHeight + 10));
        textureComponent.sizing(getHorizontalSizing(), getVerticalSizing()).positioning(Positioning.relative(50, 50));

        textureComponent.zIndex(10);

        if (positioning != null)
            componentLayout.positioning(positioning);

        componentLayout.child(button).child(textureComponent);

        return componentLayout;
    }

    public Sizing getHorizontalSizing() {
        return horizontalSizing == null ? Sizing.content() : horizontalSizing;
    }

    public Sizing getVerticalSizing() {
        return verticalSizing == null ? Sizing.content() : verticalSizing;
    }

    /**
     * Returns positioning of IconButtonComponent. Can be null if no positioning was specified.
     */
    public @Nullable Positioning getPositioning() {
        return positioning;
    }

    /**
     * Sets the sizing of IconButtonComponent
     * @param horizontalSizing the horizontal {@link Sizing}
     * @param verticalSizing the vertical {@link Sizing}
     * @return this
     */
    public IconButtonComponent sizing(Sizing horizontalSizing, Sizing verticalSizing) {
        this.verticalSizing = verticalSizing;
        this.horizontalSizing = horizontalSizing;
        return this;
    }

    /**
     * Sets the positioning of the StackLayout on the IconButtonComponent
     * @param positioning The positioning
     * @return this
     */
    public IconButtonComponent positioning(Positioning positioning) {
        this.positioning = positioning;
        return this;
    }
}
