package me.syari.ss.discord.api.utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import me.syari.ss.discord.api.exceptions.ParsingException;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;


public class DataArray implements Iterable<Object> {
    private static final Logger log = LoggerFactory.getLogger(DataObject.class);
    private static final ObjectMapper mapper;
    private static final SimpleModule module;
    private static final CollectionType listType;

    static {
        mapper = new ObjectMapper();
        module = new SimpleModule();
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
        listType = mapper.getTypeFactory().constructRawCollectionType(ArrayList.class);
    }

    protected final List<Object> data;

    protected DataArray(List<Object> data) {
        this.data = data;
    }


    @Nonnull
    public static DataArray empty() {
        return new DataArray(new ArrayList<>());
    }


    @Nonnull
    public static DataArray fromJson(@Nonnull Reader json) {
        try {
            return new DataArray(mapper.readValue(json, listType));
        } catch (IOException e) {
            throw new ParsingException(e);
        }
    }


    public int length() {
        return data.size();
    }


    public boolean isEmpty() {
        return data.isEmpty();
    }


    @Nonnull
    @SuppressWarnings("unchecked")
    public DataObject getObject(int index) {
        Map<String, Object> child = null;
        try {
            child = (Map<String, Object>) get(Map.class, index);
        } catch (ClassCastException ex) {
            log.error("Unable to extract child data", ex);
        }
        if (child == null)
            throw valueError(index, "DataObject");
        return new DataObject(child);
    }


    @Nonnull
    public String getString(int index) {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        if (value == null)
            throw valueError(index, "String");
        return value;
    }


    @Contract("_, !null -> !null")
    public String getString(int index, @Nullable String defaultValue) {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        return value == null ? defaultValue : value;
    }


    public long getLong(int index) {
        Long value = get(Long.class, index, Long::parseLong, Number::longValue);
        if (value == null)
            throw valueError(index, "long");
        return value;
    }


    public long getUnsignedLong(int index) {
        Long value = get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
        if (value == null)
            throw valueError(index, "unsigned long");
        return value;
    }


    @Nonnull
    public DataArray add(@Nullable Object value) {
        if (value instanceof SerializableData)
            data.add(((SerializableData) value).toData().data);
        else if (value instanceof DataArray)
            data.add(((DataArray) value).data);
        else
            data.add(value);
        return this;
    }


    @Nonnull
    public DataArray remove(int index) {
        data.remove(index);
        return this;
    }


    @Nonnull
    public DataArray remove(@Nullable Object value) {
        data.remove(value);
        return this;
    }

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private ParsingException valueError(int index, String expectedType) {
        return new ParsingException("Unable to resolve value at " + index + " to type " + expectedType + ": " + data.get(index));
    }

    @Nullable
    private <T> T get(@Nonnull Class<T> type, int index) {
        return get(type, index, null, null);
    }

    @Nullable
    private <T> T get(@Nonnull Class<T> type, int index, @Nullable Function<String, T> stringMapper, @Nullable Function<Number, T> numberMapper) {
        Object value = data.get(index);
        if (value == null)
            return null;
        if (type.isAssignableFrom(value.getClass()))
            return type.cast(value);
        // attempt type coercion
        if (stringMapper != null && value instanceof String)
            return stringMapper.apply((String) value);
        else if (numberMapper != null && value instanceof Number)
            return numberMapper.apply((Number) value);

        throw new ParsingException(String.format("Cannot parse value for index %d into type %s: %s instance of %s",
                index, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }

    @Nonnull
    @Override
    public Iterator<Object> iterator() {
        return data.iterator();
    }
}
