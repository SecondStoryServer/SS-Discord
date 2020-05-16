package me.syari.ss.discord.api.utils.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public enum DataType {
    INT, FLOAT, STRING, OBJECT, ARRAY, BOOLEAN, NULL;


    public boolean isType(@Nullable Object value) {
        switch (this) {
            case INT:
                return value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte;
            case FLOAT:
                return value instanceof Double || value instanceof Float;
            case STRING:
                return value instanceof String;
            case BOOLEAN:
                return value instanceof Boolean;
            case ARRAY:
                return value instanceof List;
            case OBJECT:
                return value instanceof Map;
            case NULL:
                return value == null;
            default:
                return false;
        }
    }
}
