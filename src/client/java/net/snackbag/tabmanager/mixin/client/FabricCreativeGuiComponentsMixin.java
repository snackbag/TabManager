package net.snackbag.tabmanager.mixin.client;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.snackbag.tabmanager.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents.COMMON_GROUPS;
import static net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl.TABS_PER_PAGE;

@Mixin(FabricCreativeGuiComponents.class)
abstract public class FabricCreativeGuiComponentsMixin {

    /**
     * Mixin into getPageCount to allow for fake pages in the inventory
     * @param cir
     */
    @Inject(method = "getPageCount", at = @At("HEAD"), cancellable = true)
    private static void tabmanager$getPageCount(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.ceil((ItemGroups.getGroupsToDisplay().size() - COMMON_GROUPS.stream().filter(ItemGroup::shouldDisplay).count()) / TABS_PER_PAGE) + Config.INSTANCE.fakePages + 1);
    }

}
