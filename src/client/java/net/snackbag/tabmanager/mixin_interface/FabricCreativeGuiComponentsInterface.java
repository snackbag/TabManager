package net.snackbag.tabmanager.mixin_interface;

import net.minecraft.item.ItemGroups;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;

/*? if >=1.21 {*/
import static net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl.TABS_PER_PAGE;
/*? }*/

public interface FabricCreativeGuiComponentsInterface {
    static int tabmanager$getInventoryPageCount() {
        return (int) (Math.ceil((double)
                ItemGroups.getGroups()
                        .stream()
                        .filter(
                                itemGroup -> ((ItemGroupAccessor) itemGroup).tabmanager$shouldDisplayVanilla() &&
                                        !itemGroup.isSpecial())
                        .toList()
                        .size()
                / /*? if >=1.21 {*/TABS_PER_PAGE/*?} else {*/ /*10 *//*?}*/ ) + Config.INSTANCE.fakePages);
        // ↑ Prefer Fabric's Implementation, otherwise do your own thing ↑
    }
}
