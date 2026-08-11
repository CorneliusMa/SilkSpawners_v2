package de.corneliusmay.silkspawners.plugin.metrics;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Requires;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@Requires(PluginConfig.class)
@RequiredArgsConstructor
public class MetricsHandler implements Loader {

    private static final int SERVICE_ID = 15215;

    private final Plugin plugin;

    private final LocaleHandler localeHandler;

    private final CommandUsageTracker commandUsageTracker;

    private Metrics metrics;

    // bStats is non-critical telemetry, so a failure here must never back off the whole plugin load
    @Override
    public boolean load() {
        try {
            metrics = new Metrics(plugin, SERVICE_ID);
            metrics.addCustomChart(new SimplePie("locale", localeHandler::getLocaleDisplayName));
            metrics.addCustomChart(new SimplePie(
                    "update_check", () -> PluginConfig.UPDATE_CHECK_ENABLED.get() ? "enabled" : "disabled"));
            metrics.addCustomChart(new MultiLineChart("commands_executed", commandUsageTracker::snapshot));
        } catch (RuntimeException ex) {
            Logger.error("Failed to start bStats integration", ex);
        }
        return true;
    }

    @Override
    public void unload() {
        if (metrics == null) return;
        Logger.info("Stopping bStats integration");
        metrics.shutdown();
    }
}
