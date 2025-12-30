package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ClickableFlowComponent extends FlowLayout {

    protected InvisibleButtonComponent button;

    protected boolean isActive = false;

    public ClickableFlowComponent(Sizing horizontalSizing, Sizing verticalSizing, Algorithm flowDirection, Consumer<ClickableFlowComponent> onClick) {
        super(horizontalSizing, verticalSizing, flowDirection);
        this.button = new InvisibleButtonComponent(Text.empty(), btn -> onClick.accept(this));

        this.button.sizing(Sizing.fill(), Sizing.fill())
                .positioning(io.wispforest.owo.ui.core.Positioning.relative(0, 0))
                .zIndex(10);

        this.child(this.button);
        this.surface(Surface.outline(Color.BLACK.argb()));
    }

    public ClickableFlowComponent setActive(boolean active) {
        this.isActive = active;
        this.surface(active ? Surface.outline(Color.WHITE.argb()) : Surface.outline(Color.BLACK.argb()));
        return this;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public ClickableFlowComponent toggleActive() {
        this.setActive(!this.isActive);
        return this;
    }
}
