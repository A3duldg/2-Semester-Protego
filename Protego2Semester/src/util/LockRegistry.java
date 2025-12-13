package util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class LockRegistry {
	// vi laver et HashMap, hvor Integer er nøgelen(repræsentere et id) og LockEntry er værdien
    private static final ConcurrentHashMap<Integer, LockEntry> map = new ConcurrentHashMap<>();

    private static class LockEntry {
        final Object lock = new Object();
        /* Vi bruger en AtomicInteger der starter på 1 når første tråd anmoder om den
        Den bruges til at tæller hvor mange der anvender låsen 
        */
        final AtomicInteger ref = new AtomicInteger(1);
    }
// Opretter en metode til at hente/oprette en lås for et givent id 

    public static Object acquire(int id) {
    	// opret eller hent LockEntry for id og opdater reference-tæller
        LockEntry existing = map.compute(id, (k, v) -> {
            if (v == null) return new LockEntry();
            v.ref.incrementAndGet();
            return v;
        });
        return existing.lock;
    }

    // Her har vi metoden for at frigive låsen for et givent id 
    public static void release(int id) {
        map.computeIfPresent(id, (k, v) -> {
            if (v.ref.decrementAndGet() <= 0) return null;
            return v;
        });
    }
}
