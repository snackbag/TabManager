package net.snackbag.tabmanager.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.ui.toast.ZToast;

public class ToastUtil {

    public static void displayToast(MinecraftClient client, ZToast.ZToastType type, Text title, Text description, float z) {
        ZToast.show(
                client.getToastManager(),
                type,
                title,
                description,
                z
        );
    }

}
