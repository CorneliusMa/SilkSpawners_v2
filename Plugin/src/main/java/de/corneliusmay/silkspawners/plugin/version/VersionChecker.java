package de.corneliusmay.silkspawners.plugin.version;

import com.google.common.base.Preconditions;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.utils.Schedule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Requires;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@Requires(PluginConfig.class)
@RequiredArgsConstructor
public class VersionChecker implements Loader, Dumpable {

    public static final String DOWNLOAD_URL = "https://modrinth.com/plugin/silkspawners";

    private static final URI LATEST_RELEASE_URI =
            URI.create("https://api.github.com/repos/CorneliusMa/SilkSpawners_v2/releases/latest");

    private static final Pattern RELEASE_TAG_PATTERN = Pattern.compile("\"tag_name\":\"v([0-9.]+)\"");

    private final HttpClient client = HttpClient.newHttpClient();

    private final Plugin plugin;

    private volatile String latestVersion;

    private Schedule schedule;

    public String getInstalledVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean load() {
        start();
        return true;
    }

    public Optional<String> getAvailableUpdate() {
        return Optional.ofNullable(latestVersion).filter(latest -> !isUpToDate(latest));
    }

    @Override
    public void describe(DumpObject<?> writer) {
        writer.section("plugin")
                .value("version", getInstalledVersion())
                .value("latest", latestVersion == null ? "unknown" : latestVersion)
                .value("update", updateStatus());
    }

    private String updateStatus() {
        if (!PluginConfig.UPDATE_CHECK_ENABLED.get()) return "checking disabled";
        String latest = latestVersion;
        if (latest == null) return "unknown";
        return isUpToDate(latest) ? "up to date" : "available";
    }

    public synchronized void restart() {
        Optional<Duration> interval = configuredInterval();
        if (schedule != null && interval.isPresent() && schedule.isRunning(interval.get())) return;
        stop();
        start();
    }

    public synchronized void stop() {
        if (schedule == null) return;
        Logger.info("Stopping version checker");
        schedule.stop();
        schedule = null;
    }

    @Override
    public void unload() {
        stop();
    }

    private synchronized void start() {
        Optional<Duration> interval = configuredInterval();
        if (interval.isPresent()) start(interval.get());
        else Logger.warn("Update checking is disabled");
    }

    private Optional<Duration> configuredInterval() {
        if (!PluginConfig.UPDATE_CHECK_ENABLED.get()) return Optional.empty();
        return Optional.of(Duration.ofHours(PluginConfig.UPDATE_CHECK_INTERVAL.get()));
    }

    private void start(Duration interval) {
        Preconditions.checkState(schedule == null);
        schedule = new Schedule("SilkSpawners-VersionChecker", interval, this::check);
    }

    private void check() throws InterruptedException {
        Logger.info("Checking for updates");
        Optional<String> fetched = fetchLatestVersion();
        if (fetched.isEmpty()) return;
        String latest = fetched.get();
        latestVersion = latest;
        if (isUpToDate(latest)) Logger.info(upToDateMessage(latest));
        else Logger.warn(updateAvailableMessage(latest));
    }

    private Optional<String> fetchLatestVersion() throws InterruptedException {
        try {
            HttpRequest request =
                    HttpRequest.newBuilder().uri(LATEST_RELEASE_URI).GET().build();
            String body =
                    client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            Matcher matcher = RELEASE_TAG_PATTERN.matcher(body);
            if (matcher.find()) return Optional.of(matcher.group(1));
            Logger.error("Error getting latest version");
            return Optional.empty();
        } catch (IOException ex) {
            Logger.error("Error getting latest version", ex);
            return Optional.empty();
        }
    }

    private boolean isUpToDate(String latestVersion) {
        int[] installed = parseVersion(getInstalledVersion());
        int[] latest = parseVersion(latestVersion);
        for (int i = 0; i < latest.length; i++) {
            int installedPart = i < installed.length ? installed[i] : 0;
            if (latest[i] != installedPart) return latest[i] < installedPart;
        }
        return true;
    }

    private int[] parseVersion(String version) {
        return Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    private String updateAvailableMessage(String latestVersion) {
        return "§eUpdate available! Download at " + DOWNLOAD_URL + " §f\nInstalled version: v"
                + getInstalledVersion()
                + "\nLatest version: v"
                + latestVersion;
    }

    private String upToDateMessage(String latestVersion) {
        return "The plugin is up to date (Current release v" + latestVersion + ")";
    }
}
