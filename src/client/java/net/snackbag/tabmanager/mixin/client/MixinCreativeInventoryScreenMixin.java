package net.snackbag.tabmanager.mixin.client;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.snackbag.tabmanager.access.CreativeInventoryScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"MixinAnnotationTarget"})
@Mixin(value = CreativeInventoryScreen.class, priority = 1500)
public abstract class MixinCreativeInventoryScreenMixin implements CreativeInventoryScreenAccessor {

    @Shadow(remap = false) // FAPI
    private static int currentPage;

    @Override
    public void tabmanager$setCurrentPage(int page) {
        currentPage = page;
    }

}
