package de.corneliusmay.silkspawners.plugin.dump.sections;

import de.corneliusmay.silkspawners.plugin.dump.DumpEntry;
import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Wired;
import org.weftkit.wiring.runtime.WeftLoader;

@Wired
@RequiredArgsConstructor
public class WiringSection implements Dumpable {

    private final WeftLoader loader;

    @Override
    public void describe(DumpObject<?> writer) {
        DumpEntry<?> section = writer.section("wiring");
        section.value("singletons", loader.loadOrder().size());
        DumpEntry<?> timings = section.section("load-timings");
        loader.loadTimings()
                .forEach((type, duration) -> timings.value(type.getSimpleName(), duration.toMillis() + "ms"));
    }
}
