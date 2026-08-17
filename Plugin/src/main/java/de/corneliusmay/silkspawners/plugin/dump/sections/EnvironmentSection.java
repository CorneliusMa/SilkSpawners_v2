package de.corneliusmay.silkspawners.plugin.dump.sections;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import org.weftkit.wiring.Wired;

@Wired
class EnvironmentSection implements Dumpable {

    @Override
    public void describe(DumpObject<?> writer) {
        writer.section("environment")
                .section("java")
                .value("version", System.getProperty("java.version"))
                .value("vendor", System.getProperty("java.vendor"))
                .end()
                .section("os")
                .value("name", System.getProperty("os.name"))
                .value("version", System.getProperty("os.version"))
                .value("arch", System.getProperty("os.arch"));
    }
}
