package net.snackbag.tabmanager.ui.toast;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that wraps {@link net.minecraft.client.toast.SystemToast} to provide a way of setting the Z index of such toast and provides general-purpose Toast Types.
 */
public class ZToast extends SystemToast {

    public final float z;

    public ZToast(Type type, Text title, @Nullable Text description, float z) {
        super(type, title, description);
        this.z = z;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.push();
        context.translate(0, 0, this.z);
        Visibility visibility = super.draw(context, manager, startTime);
        context.pop();
        return visibility;
    }

    public static void show(
            @NotNull ToastManager manager,
            ZToastType type,
            Text title,
            Text description,
            float z
    ) {
        manager.add(new ZToast(type, title, description, z));
    }

    public static class ZToastType extends SystemToast.Type {
        public ZToastType(long duration) {
            super(duration);
        }

        public ZToastType() {
            super();
        }

        public static final ZToast.ZToastType INFO = new ZToastType();
        public static final ZToast.ZToastType WARNING = new ZToastType();
        public static final ZToast.ZToastType ERROR = new ZToastType();
    }
}
