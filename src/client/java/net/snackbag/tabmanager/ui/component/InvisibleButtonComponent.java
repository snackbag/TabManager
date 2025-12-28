package net.snackbag.tabmanager.ui.component;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class InvisibleButtonComponent extends ButtonComponent {

    protected Renderer INVISIBLE = (matrices, button, delta) -> NinePatchTexture.draw(null, matrices, button.getX(), button.getY(), button.getWidth(), button.getHeight());

    public InvisibleButtonComponent(Text message, Consumer<ButtonComponent> onPress) {
        super(message, onPress);
        this.renderer = INVISIBLE;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}
