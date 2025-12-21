package net.snackbag.tabmanager.exception;

import com.google.gson.JsonParseException;

public class ConfigParseException extends JsonParseException {
    public ConfigParseException(String message) {
        super(message);
    }
}
