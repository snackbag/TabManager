package net.snackbag.tabmanager.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.client.gui.DrawContext;
import net.snackbag.tabmanager.access.ButtonWidgetAccessor;
import net.snackbag.tabmanager.access.CreativeInventoryScreenAccessor;
import net.snackbag.tabmanager.util.CreativeMenuUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*? if >1.21 {*/
/*import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.snackbag.tabmanager.mixin_interface.FabricCreativeGuiComponentsInterface;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
*//*?} else {*/
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
/*?}*/

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class FabricItemGroupButtonWidgetMixin {

    /*? if >1.21 {*/
    /*@Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void tabmanager$onInit(int x, int y, FabricCreativeGuiComponents.Type type, CreativeInventoryScreen screen, CallbackInfo ci) {
        ((ButtonWidgetAccessor) this).tabmanager$setOnPress((bw) -> {
            int toPage = type == FabricCreativeGuiComponents.Type.NEXT ?
                    screen.getCurrentPage() + 1 :
                    screen.getCurrentPage() - 1;
            
            if (toPage < 0 || toPage >= screen.getPageCount()) // >= because getPageCount is 1-based and toPage is 0-based
                return;

            ((CreativeInventoryScreenAccessor) screen).tabmanager$setCurrentPage(toPage);
        });
    }
    *//*? } else {*/
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void tabmanager$onInit(int x, int y, FabricCreativeGuiComponents.Type type, CreativeGuiExtensions extensions, CallbackInfo ci) {
        ((ButtonWidgetAccessor) this).tabmanager$setOnPress((bw) -> {
            int toPage = type == FabricCreativeGuiComponents.Type.NEXT ?
                    extensions.fabric_currentPage() + 1 :
                    extensions.fabric_currentPage() - 1;

            if (toPage < 0 || toPage >= CreativeMenuUtility.getPageCount()) // >= because getPageCount is 1-based and toPage is 0-based
                return;

            ((CreativeInventoryScreenAccessor) extensions).tabmanager$setCurrentPage(toPage);
        });
    }

    @Inject(
            method = "render",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;II)V"
            )
    )
    private void tabmanager$modifyPageCount(
            DrawContext drawContext,
            int mouseX, int mouseY,
            float float_1, CallbackInfo ci,
            @Local(name = "pageCount") int pageCount
    ) {
        pageCount = CreativeMenuUtility.getPageCount();
    }
    /*?}*/
}
