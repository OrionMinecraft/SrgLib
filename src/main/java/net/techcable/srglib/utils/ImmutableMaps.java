package net.techcable.srglib.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public final class ImmutableMaps {
    private ImmutableMaps() {
    }

    public static <K, V> Map<K, V> createBiMap(Set<K> keys, Function<K, V> valueFunction) {
        return createMap(keys, valueFunction);
    }

    public static <K, V> Map<K, V> createMap(Set<K> keys, Function<K, V> valueFunction) {
        Map<K, V> map = new HashMap<>();
        keys.forEach((key) -> map.put(key, valueFunction.apply(key)));
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<V, K> inverse(Map<K, V> input) {
        Map<V, K> newMap = new HashMap<>(input.size());
        input.forEach((key, value) -> {
            newMap.put(value, key);
        });

        return Collections.unmodifiableMap(newMap);
    }

    public static <K, V> String joinToString(
            Map<K, V> map,
            BiFunction<K, V, String> asString,
            String delimiter,
            String prefix,
            String suffix
    ) {
        int size = requireNonNull(map, "Null list").size();
        int delimiterLength = requireNonNull(delimiter, "Null delimiter").length();
        int prefixLength = requireNonNull(prefix, "Null prefix").length();
        int suffixLength = requireNonNull(suffix, "Null suffix").length();
        String[] strings = new String[size];
        int neededChars = prefixLength + suffixLength + (size - 1) * delimiterLength;
        int index = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            String str = asString.apply(key, value);
            strings[index++] = str;
            neededChars += str.length();
        }
        char[] result = new char[neededChars];
        int resultSize = 0;
        prefix.getChars(0, prefixLength, result, resultSize);
        resultSize += prefixLength;
        for (int i = 0; i < size; i++) {
            String str = strings[i];
            if (i > 0) {
                // Prefix it with the delimiter
                delimiter.getChars(0, delimiterLength, result, resultSize);
                resultSize += delimiterLength;
            }
            int length = str.length();
            str.getChars(0, length, result, resultSize);
            resultSize += length;
        }
        suffix.getChars(0, suffixLength, result, resultSize);
        resultSize += suffixLength;
        assert result.length == resultSize;
        return String.valueOf(result);
    }
}
