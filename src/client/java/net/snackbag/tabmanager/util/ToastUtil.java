package net.snackbag.tabmanager.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.snackbag.tabmanager.ui.toast.ZToast;

public class ToastUtil {

    public static void displayToast(
            MinecraftClient client,
            //? if =1.21.1
            /*ZToast.ZToastType type,*/
            Text title,
            Text description,
            float z
    ) {
        ZToast.show(
                client.getToastManager(),
                //? if =1.21.1
                /*type,*/
                title,
                description,
                z
        );
    }

}
