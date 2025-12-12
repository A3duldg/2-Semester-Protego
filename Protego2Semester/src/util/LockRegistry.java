package util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class LockRegistry {
    private static final ConcurrentHashMap<Integer, LockEntry> map = new ConcurrentHashMap<>();

    private static class LockEntry {
        final Object lock = new Object();
        final AtomicInteger ref = new AtomicInteger(1);
    }

    public static Object acquire(int id) {
        LockEntry existing = map.compute(id, (k, v) -> {
            if (v == null) return new LockEntry();
            v.ref.incrementAndGet();
            return v;
        });
        return existing.lock;
    }

    public static void release(int id) {
        map.computeIfPresent(id, (k, v) -> {
            if (v.ref.decrementAndGet() <= 0) return null;
            return v;
        });
    }
}
