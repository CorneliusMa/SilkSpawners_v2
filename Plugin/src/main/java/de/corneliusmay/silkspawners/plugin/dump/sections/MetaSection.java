package de.corneliusmay.silkspawners.plugin.dump.sections;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class MetaSection implements Dumpable {

    @Override
    public void describe(DumpObject<?> writer) {
        writer.section("meta").value("generated", Instant.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
