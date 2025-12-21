package net.snackbag.tabmanager.exception;

import com.google.gson.JsonParseException;

public class ItemFilterParseException extends JsonParseException {

    public ItemFilterParseException(String msg) {
        super(msg);
    }

}
