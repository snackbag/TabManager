package net.snackbag.tabmanager.mixin.client;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.snackbag.tabmanager.access.ButtonWidgetAccessor;
import org.spongepowered.asm.mixin.*;

@Mixin(ButtonWidget.class)
public abstract class ButtonWidgetMixin implements ButtonWidgetAccessor {

    @Shadow @Final @Mutable
    protected ButtonWidget.PressAction onPress;

    @Override
    public void tabmanager$setOnPress(ButtonWidget.PressAction onPress) {
        this.onPress = onPress;
    }
}
