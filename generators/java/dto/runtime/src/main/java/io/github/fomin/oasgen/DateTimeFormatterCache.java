package io.github.fomin.oasgen;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DateTimeFormatterCache {
    private static final Map<String, DateTimeFormatter> cache = new ConcurrentHashMap<>();

    public static DateTimeFormatter get(String pattern) {
        return cache.computeIfAbsent(pattern, s -> DateTimeFormatter.ofPattern(pattern));
    }
}
