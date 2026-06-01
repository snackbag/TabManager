package net.snackbag.tabmanager.mixin.client;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.item.ItemGroups;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.mixin_interface.FabricCreativeGuiComponentsInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;

/*? if >=1.21 {*/
/*import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl.TABS_PER_PAGE;
*//*? }*/

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricCreativeGuiComponents.class)
abstract public class FabricCreativeGuiComponentsMixin implements FabricCreativeGuiComponentsInterface {

    /*? if >=1.21 {*/
    /*// Mixin into getPageCount to allow for fake pages in the inventory
    @Inject(method = "getPageCount", at = @At("HEAD"), cancellable = true)
    private static void tabmanager$getPageCount(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FabricCreativeGuiComponentsInterface.tabmanager$getInventoryPageCount());
    }
    *//*?}*/

}
