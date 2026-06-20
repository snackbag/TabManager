package net.snackbag.tabmanager.mixin.client;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.snackbag.tabmanager.access.CreativeInventoryScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings({"MixinAnnotationTarget"})
@Mixin(value = CreativeInventoryScreen.class, priority = 1500)
public abstract class MixinCreativeInventoryScreenMixin implements CreativeInventoryScreenAccessor {

    /*? if >=1.21 {*/
    @Shadow(remap = false) // FAPI
    private static int currentPage;
    /*?} else {*/
    /*@Shadow(remap = false)
    private static int fabric_currentPage;
    *//*?}*/

    @Override
    public void tabmanager$setCurrentPage(int page) {
        /*? if >=1.21 {*/
        currentPage = page;
        /*?} else {*/
        /*fabric_currentPage = page;
        *//*?}*/
    }

}
