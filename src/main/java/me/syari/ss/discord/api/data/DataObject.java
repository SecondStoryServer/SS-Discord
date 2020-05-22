package me.syari.ss.discord.api.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.syari.ss.discord.api.exceptions.ParsingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.*;
import java.util.function.Function;

public class DataObject {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleModule module = new SimpleModule();

    static {
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
    }

    final DataContainer container;

    public Map<String, Object> getData() {
        return container.getData();
    }

    DataObject(@NotNull DataContainer container) {
        this.container = container;
    }

    @NotNull
    public static DataObject empty() {
        return new DataObject(new DataContainer());
    }

    @NotNull
    public static DataObject fromJson(@NotNull String json) {
        DataContainer container = DataContainer.Companion.fromJson(json);
        return new DataObject(container);
    }

    @NotNull
    public static DataObject fromJson(@NotNull Reader stream) {
        DataContainer container = DataContainer.Companion.fromJson(stream);
        return new DataObject(container);
    }

    public boolean contains(@NotNull String key) {
        return container.contains(key);
    }

    @NotNull
    public DataObject getContainerOrThrow(@NotNull String key) {
        DataObject value = getContainer(key);
        if (value == null) throw valueError(key, "DataObject");
        return value;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public DataObject getContainer(@NotNull String key) {
        Map<String, Object> child = null;
        try {
            child = (Map<String, Object>) get(Map.class, key);
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
        return child != null ? new DataObject(new DataContainer(child)) : null;
    }

    @NotNull
    public DataArray getArrayOrThrow(@NotNull String key) {
        DataArray value = getArray(key);
        if (value == null) throw valueError(key, "DataArray");
        return value;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public DataArray getArray(@NotNull String key) {
        List<Object> child = null;
        try {
            child = (List<Object>) get(List.class, key);
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
        return child != null ? new DataArray(child) : null;
    }

    @Nullable
    public Object get(@NotNull String key) {
        return container.get(key);
    }

    @Nullable
    public String getString(@NotNull String key) {
        return container.getString(key);
    }

    @NotNull
    public String getStringOrThrow(@NotNull String key) {
        return container.getStringOrThrow(key);
    }

    @Nullable
    public Boolean getBoolean(@NotNull String key) {
        return container.getBoolean(key);
    }

    @Nullable
    public Long getLong(@NotNull String key) {
        return container.getLong(key);
    }

    public long getLongOrThrow(@NotNull String key) {
        return container.getLongOrThrow(key);
    }

    @Nullable
    public Integer getInt(@NotNull String key) {
        return container.getInt(key);
    }

    public int getIntOrThrow(@NotNull String key) {
        return container.getIntOrThrow(key);
    }

    public void remove(@NotNull String key) {
        container.remove(key);
    }

    public void put(@NotNull String key, @Nullable Object value) {
        if (value instanceof DataObject) getData().put(key, ((DataObject) value).toData().getData());
        else if (value instanceof DataArray) getData().put(key, ((DataArray) value).data);
        else getData().put(key, value);
    }

    @NotNull
    public Set<String> keys() {
        return container.getKeys();
    }

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(getData());
        } catch (JsonProcessingException ex) {
            throw new ParsingException(ex);
        }
    }

    @NotNull
    public DataObject toData() {
        return this;
    }

    @Contract("_, _ -> new")
    private @NotNull
    ParsingException valueError(String key, String expectedType) {
        return new ParsingException("Unable to resolve value with key " + key + " to type " + expectedType + ": " + getData().get(key));
    }

    @Nullable
    private <T> T get(@NotNull Class<T> type, @NotNull String key) {
        return get(type, key, null, null);
    }

    @Nullable
    private <T> T get(@NotNull Class<T> type, @NotNull String key, @Nullable Function<String, T> stringParse, @Nullable Function<Number, T> numberParse) {
        Object value = getData().get(key);
        if (value == null) return null;
        if (type.isAssignableFrom(value.getClass())) return type.cast(value);
        if (value instanceof Number && numberParse != null) return numberParse.apply((Number) value);
        else if (value instanceof String && stringParse != null) return stringParse.apply((String) value);
        throw new ParsingException(String.format("Cannot parse value for %s into type %s: %s instance of %s", key, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }
}
