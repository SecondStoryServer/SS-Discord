package me.syari.ss.discord.api.utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.syari.ss.discord.api.exceptions.ParsingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class DataArray implements Iterable<Object> {
    private static final Logger log = LoggerFactory.getLogger(DataObject.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleModule module = new SimpleModule();

    static {
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
    }

    protected final List<Object> data;

    protected DataArray(List<Object> data) {
        this.data = data;
    }

    @NotNull
    public static DataArray empty() {
        return new DataArray(new ArrayList<>());
    }

    public int length() {
        return data.size();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public DataObject getObject(int index) {
        Map<String, Object> child = null;
        try {
            child = get(Map.class, index, null, null);
        } catch (ClassCastException ex) {
            log.error("Unable to extract child data", ex);
        }
        if (child == null)
            throw valueError(index, "DataObject");
        return new DataObject(child);
    }

    @NotNull
    public String getString(int index) {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        if (value == null)
            throw valueError(index, "String");
        return value;
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

    @NotNull
    public DataArray add(@Nullable Object value) {
        if (value instanceof SerializableData)
            data.add(((SerializableData) value).toData().data);
        else if (value instanceof DataArray)
            data.add(((DataArray) value).data);
        else
            data.add(value);
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

    @Contract("_, _ -> new")
    private @NotNull ParsingException valueError(int index, String expectedType) {
        return new ParsingException("Unable to resolve value at " + index + " to type " + expectedType + ": " + data.get(index));
    }

    @Nullable
    private <T> T get(@NotNull Class<T> type, int index, @Nullable Function<String, T> stringMapper, @Nullable Function<Number, T> numberMapper) {
        Object value = data.get(index);
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        if (stringMapper != null && value instanceof String) {
            return stringMapper.apply((String) value);
        } else if (numberMapper != null && value instanceof Number) {
            return numberMapper.apply((Number) value);
        }

        throw new ParsingException(String.format("Cannot parse value for index %d into type %s: %s instance of %s", index, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return data.iterator();
    }
}
