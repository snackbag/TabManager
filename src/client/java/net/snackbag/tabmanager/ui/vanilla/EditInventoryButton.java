package net.snackbag.tabmanager.ui.vanilla;

/*? if <1.20.3 {*/
/*import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
*//*?}*/

//? if >=1.20.3
import net.minecraft.client.gui.screen.ButtonTextures;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.TabManagerClient;

@SuppressWarnings("commented-out-code")
public class EditInventoryButton extends TexturedButtonWidget {

    public static final int EDIT_BUTTON_WIDTH_HEIGHT = 20;

    /*?if >=1.20.3 {*/
    private static final ButtonTextures BUTTON_TEXTURES =
            new ButtonTextures(
                    Identifier.of(TabManagerClient.MOD_ID, "widget/edit_button"),
                    Identifier.of(TabManagerClient.MOD_ID, "widget/edit_button_highlighted")
            );

    public EditInventoryButton(int x, int y, PressAction pressAction) {
        super(x, y, EDIT_BUTTON_WIDTH_HEIGHT, EDIT_BUTTON_WIDTH_HEIGHT, BUTTON_TEXTURES, pressAction);
    }
    /*?}*/

    /*?if <1.20.3 {*/
    /*public EditInventoryButton(int x, int y, PressAction pressAction) {
        super(x, y, EDIT_BUTTON_WIDTH_HEIGHT, EDIT_BUTTON_WIDTH_HEIGHT, 0, 0, Icon.ICON.texture, pressAction);
        this.setTooltip(Tooltip.of(Text.translatable("tabmanager.gui.edit_button.tooltip")));
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);

        Icon icon;

        if (this.isHovered())
            icon = Icon.ICON;
        else
            icon = Icon.ICON_HOVERED;

        // U and V 0 because each is its own file
        context.drawTexture(icon.texture, this.getX(), this.getY(), 0, 0, this.width, this.height);
    }


    private enum Icon {
        ICON(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/widget/edit_button.png")),
        ICON_HOVERED(Identifier.of(TabManagerClient.MOD_ID, "textures/gui/sprites/widget/edit_button_highlighted.png"));

        final Identifier texture;

        Icon(Identifier texture) {
            this.texture = texture;
        }
    }
    *//*?}*/
}
