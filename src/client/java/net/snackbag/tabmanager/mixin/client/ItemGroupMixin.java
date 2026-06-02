package net.snackbag.tabmanager.mixin.client;

/*? if >=1.21 {*/
/*import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
*//*?} else if <1.21 {*/
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
/*?}*/

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.snackbag.tabmanager.access.ItemGroupAccessor;
import net.snackbag.tabmanager.config.Config;
import net.snackbag.tabmanager.util.ItemFilter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ItemGroup.class)
abstract public class ItemGroupMixin implements ItemGroupAccessor {

    // SHADOWED FIELDS ---------------------------

    @Shadow @Final @Mutable
    private ItemGroup.Row row;

    @Shadow @Final @Mutable
    private int column;

    @Shadow
    private @Nullable ItemStack icon;

    // The original unmodified display stacks
    @Shadow
    private Collection<ItemStack> displayStacks;

    @Shadow @Final
    private ItemGroup.Type type;



    // UNIQUE FIELDS -----------------------------

    @Unique
    private Identifier tabmanager$tabKey;

    @Unique
    private boolean tabmanager$isHidden = false;

    // The original unmodified display stacks are always kept in the original class (see @Shadow above)
    // This is the modified version with all the filters applied
    @Unique
    private List<ItemStack> tabmanager$displayStacks = new CopyOnWriteArrayList<>();




    // INJECTIONS --------------------------------

    /**
     * Cancels the display of the tab if it is marked as hidden.
     */
    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void shouldDisplay(CallbackInfoReturnable<Boolean> cir) {
        if (tabmanager$isHidden)
            cir.setReturnValue(false);
    }


    @Inject(method = "updateEntries", at = @At("TAIL"))
    private void updateEntries(ItemGroup.DisplayContext displayContext, CallbackInfo ci) {
        // Apply filters after update here
        this.tabmanager$displayStacks = new CopyOnWriteArrayList<>(this.displayStacks); // Reset to all items first
        Config.reload(); // Then re-apply filters (reload the entire config)
    }

    /**
     * Overrides the display stacks with the filtered ones if they exist.
     */
    @Inject(method = "getDisplayStacks", at = @At("HEAD"), cancellable = true)
    private void getDisplayStacks(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        if (tabmanager$displayStacks != null)
            cir.setReturnValue(tabmanager$displayStacks);
    }




    // GETTERS / SETTERS -------------------------

    @Override
    public Identifier tabmanager$getTabKey() {
        return tabmanager$tabKey;
    }

    @Override
    public void tabmanager$setTabKey(Identifier id) {
        this.tabmanager$tabKey = id;
    }

    @Override
    public boolean tabmanager$isHidden() {
        return tabmanager$isHidden;
    }

    @Override
    public void tabmanager$setHidden(boolean hidden) {
        tabmanager$isHidden = hidden;
    }

    @Override
    public void tabmanager$setColumn(int column) {
        this.column = column;
    }

    @Override
    public void tabmanager$setRow(ItemGroup.Row row) {
        this.row = row;
    }

    @Override
    public void tabmanager$setIcon(ItemStack istack) {
        this.icon = istack;
    }

    @Override
    public void tabmanager$applyFilterDisplayItems(ItemFilter filter) {
        if (!filter.getApplicableGroups().contains((ItemGroup)(Object)this)) return;
        for (ItemStack istack : this.tabmanager$displayStacks) {
            if (!tabmanager$displayStacks.contains(istack)) continue; // Avoid removing non-existent
            String itemId = istack.getItem().toString();
            if (itemId != null && filter.matches(itemId))
                tabmanager$displayStacks.remove(istack);
        }
    }

    @Override
    public void tabmanager$resetDisplayItems() {
        tabmanager$displayStacks = new CopyOnWriteArrayList<>(this.displayStacks);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public int tabmanager$getPage() {
        //? if >=1.21 {
        /*return ((FabricItemGroupImpl) this).fabric_getPage();
        *///?} else if <1.21 {
        return ((FabricItemGroup) this).getPage();
        //?}
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void tabmanager$setPage(int page) {
        //? if >=1.21 {
        /*((FabricItemGroupImpl) this).fabric_setPage(page);
        *///?} else if <1.21 {
        ((FabricItemGroup) this).setPage(page);
        //?}
    }

    @Override
    public boolean tabmanager$shouldDisplayVanilla() {
        return this.tabmanager$getType() != ItemGroup.Type.CATEGORY || !this.displayStacks.isEmpty();
    }

    @Override
    public ItemGroup.Type tabmanager$getType() {
        return this.type;
    }
}
