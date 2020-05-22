package me.syari.ss.discord.api.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.syari.ss.discord.api.exceptions.ParsingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class DataArray implements Iterable<Object> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleModule module = new SimpleModule();

    static {
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
    }

    final List<Object> data;

    DataArray(List<Object> data) {
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
    public DataContainer getObject(int index) {
        Map<String, Object> child = null;
        try {
            child = get(Map.class, index, null, null);
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
        if (child == null)
            throw valueError(index, "DataContainer");
        return new DataContainer(child);
    }

    public long getLong(int index) {
        Long value = get(Long.class, index, Long::parseLong, Number::longValue);
        if (value == null)
            throw valueError(index, "long");
        return value;
    }

    @NotNull
    public DataArray add(@Nullable Object value) {
        if (value instanceof DataContainer)
            data.add(((DataContainer) value).getData());
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
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Contract("_, _ -> new")
    private @NotNull
    ParsingException valueError(int index, String expectedType) {
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
