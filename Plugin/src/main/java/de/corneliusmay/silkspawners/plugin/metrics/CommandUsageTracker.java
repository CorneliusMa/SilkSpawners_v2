package de.corneliusmay.silkspawners.plugin.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
public class CommandUsageTracker {

    private final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();

    public void record(String command) {
        counts.computeIfAbsent(command, key -> new AtomicInteger()).incrementAndGet();
    }

    public Map<String, Integer> snapshot() {
        Map<String, Integer> snapshot = new HashMap<>();
        counts.forEach((command, count) -> {
            int value = count.getAndSet(0);
            if (value > 0) snapshot.put(command, value);
        });
        return snapshot;
    }
}
