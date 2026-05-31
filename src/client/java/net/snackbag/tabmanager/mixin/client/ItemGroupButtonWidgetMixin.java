package net.snackbag.tabmanager.mixin.client;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*? if >=1.21 {*/
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import static net.snackbag.tabmanager.util.CreativeMenuUtility.getPageCount;
/*? } else if <1.21 {*/
/*import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.mixin_interface.FabricCreativeGuiComponentsInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
*//*?}*/

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
abstract public class ItemGroupButtonWidgetMixin {

    /*? if >=1.21 {*/
    // Allows the buttons to be displayed on fake pages.
    @Redirect(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;hasAdditionalPages()Z"
            )
    )
    private boolean tabmanager$redirectHasAdditionalPages(CreativeInventoryScreen creativeInventoryScreen) {
        return getPageCount() > 1;
    }
    /*?} else if <1.21 {*/
    /*@Shadow
    @Final
    CreativeGuiExtensions extensions;

    @Redirect(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;II)V"
            )
    )
    private void redirectPageCountText(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y) {
        int pageCount = FabricCreativeGuiComponentsInterface.tabmanager$getInventoryPageCount();
        instance.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("fabric.gui.creativeTabPage", extensions.fabric_currentPage() + 1, pageCount), x, y);
    }
    *//*?}*/

}
