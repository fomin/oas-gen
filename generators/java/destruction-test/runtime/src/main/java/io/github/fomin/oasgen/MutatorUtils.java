package io.github.fomin.oasgen;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class MutatorUtils {
    private static final Random RND = new Random();

    public static <T> T random(T[] collection, T... exclude) {
        return random(collection, Arrays.asList(exclude));
    }

    public static <T> T random(T[] collection, Collection<T> exclude) {
        if (exclude.size() == 0) {
            return collection[RND.nextInt(collection.length)];
        }
        return random(Arrays.stream(collection).filter(i -> exclude.stream().noneMatch(i::equals)).collect(Collectors.toList()));
    }

    public static <T> T random(Collection<T> collection) {
        int index = RND.nextInt(collection.size());
        Iterator<T> iter = collection.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
}
