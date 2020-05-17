package me.syari.ss.discord.api.utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.MapType;
import me.syari.ss.discord.api.exceptions.ParsingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;


public class DataObject implements SerializableData {
    private static final Logger log = LoggerFactory.getLogger(DataObject.class);
    private static final ObjectMapper mapper;
    private static final SimpleModule module;
    private static final MapType mapType;

    static {
        mapper = new ObjectMapper();
        module = new SimpleModule();
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
        mapType = mapper.getTypeFactory().constructRawMapType(HashMap.class);
    }

    protected final Map<String, Object> data;

    protected DataObject(@NotNull Map<String, Object> data) {
        this.data = data;
    }


    @NotNull
    public static DataObject empty() {
        return new DataObject(new HashMap<>());
    }


    @NotNull
    public static DataObject fromJson(@NotNull String json) {
        try {
            Map<String, Object> map = mapper.readValue(json, mapType);
            return new DataObject(map);
        } catch (IOException ex) {
            throw new ParsingException(ex);
        }
    }


    @NotNull
    public static DataObject fromJson(@NotNull Reader stream) {
        try {
            Map<String, Object> map = mapper.readValue(stream, mapType);
            return new DataObject(map);
        } catch (IOException ex) {
            throw new ParsingException(ex);
        }
    }


    public boolean hasKey(@NotNull String key) {
        return data.containsKey(key);
    }


    public boolean isNull(@NotNull String key) {
        return data.get(key) == null;
    }


    public boolean isType(@NotNull String key, @NotNull DataType type) {
        return type.isType(data.get(key));
    }


    @NotNull
    public DataObject getObject(@NotNull String key) {
        return optObject(key).orElseThrow(() -> valueError(key, "DataObject"));
    }


    @NotNull
    @SuppressWarnings("unchecked")
    public Optional<DataObject> optObject(@NotNull String key) {
        Map<String, Object> child = null;
        try {
            child = (Map<String, Object>) get(Map.class, key);
        } catch (ClassCastException ex) {
            log.error("Unable to extract child data", ex);
        }
        return child == null ? Optional.empty() : Optional.of(new DataObject(child));
    }


    @NotNull
    public DataArray getArray(@NotNull String key) {
        return optArray(key).orElseThrow(() -> valueError(key, "DataArray"));
    }


    @NotNull
    @SuppressWarnings("unchecked")
    public Optional<DataArray> optArray(@NotNull String key) {
        List<Object> child = null;
        try {
            child = (List<Object>) get(List.class, key);
        } catch (ClassCastException ex) {
            log.error("Unable to extract child data", ex);
        }
        return child == null ? Optional.empty() : Optional.of(new DataArray(child));
    }


    @NotNull
    public Optional<Object> opt(@NotNull String key) {
        return Optional.ofNullable(data.get(key));
    }


    @NotNull
    public Object get(@NotNull String key) {
        Object value = data.get(key);
        if (value == null)
            throw valueError(key, "any");
        return value;
    }


    @NotNull
    public String getString(@NotNull String key) {
        String value = getString(key, null);
        if (value == null)
            throw valueError(key, "String");
        return value;
    }


    @Contract("_, !null -> !null")
    public String getString(@NotNull String key, @Nullable String defaultValue) {
        String value = get(String.class, key, UnaryOperator.identity(), String::valueOf);
        return value == null ? defaultValue : value;
    }


    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }


    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        Boolean value = get(Boolean.class, key, Boolean::parseBoolean, null);
        return value == null ? defaultValue : value;
    }


    public long getLong(@NotNull String key) {
        Long value = get(Long.class, key, Long::parseLong, Number::longValue);
        if (value == null)
            throw valueError(key, "long");
        return value;
    }


    public long getLong(@NotNull String key, long defaultValue) {
        Long value = get(Long.class, key, Long::parseLong, Number::longValue);
        return value == null ? defaultValue : value;
    }


    public long getUnsignedLong(@NotNull String key) {
        Long value = get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
        if (value == null)
            throw valueError(key, "unsigned long");
        return value;
    }


    public long getUnsignedLong(@NotNull String key, long defaultValue) {
        Long value = get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
        return value == null ? defaultValue : value;
    }


    public int getInt(@NotNull String key) {
        Integer value = get(Integer.class, key, Integer::parseInt, Number::intValue);
        if (value == null)
            throw valueError(key, "int");
        return value;
    }


    public void remove(@NotNull String key) {
        data.remove(key);
    }


    @NotNull
    public DataObject put(@NotNull String key, @Nullable Object value) {
        if (value instanceof SerializableData)
            data.put(key, ((SerializableData) value).toData().data);
        else if (value instanceof DataArray)
            data.put(key, ((DataArray) value).data);
        else
            data.put(key, value);
        return this;
    }


    @NotNull
    public Set<String> keys() {
        return data.keySet();
    }

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            throw new ParsingException(ex);
        }
    }


    @NotNull
    @Override
    public DataObject toData() {
        return this;
    }

    private ParsingException valueError(String key, String expectedType) {
        return new ParsingException("Unable to resolve value with key " + key + " to type " + expectedType + ": " + data.get(key));
    }

    @Nullable
    private <T> T get(@NotNull Class<T> type, @NotNull String key) {
        return get(type, key, null, null);
    }

    @Nullable
    private <T> T get(@NotNull Class<T> type, @NotNull String key, @Nullable Function<String, T> stringParse, @Nullable Function<Number, T> numberParse) {
        Object value = data.get(key);
        if (value == null)
            return null;
        if (type.isAssignableFrom(value.getClass()))
            return type.cast(value);
        if (value instanceof Number && numberParse != null)
            return numberParse.apply((Number) value);
        else if (value instanceof String && stringParse != null)
            return stringParse.apply((String) value);

        throw new ParsingException(String.format("Cannot parse value for %s into type %s: %s instance of %s",
                key, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }
}
