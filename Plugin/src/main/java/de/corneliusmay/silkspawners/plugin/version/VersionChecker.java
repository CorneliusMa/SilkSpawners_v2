package de.corneliusmay.silkspawners.plugin.version;

import com.google.common.base.Preconditions;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Wired
@RequiredArgsConstructor
public class VersionChecker {

    private static final URI LATEST_RELEASE_URI =
            URI.create("https://api.github.com/repos/CorneliusMa/SilkSpawners_v2/releases/latest");

    @Getter
    private final String installedVersion;

    @Getter
    private volatile String latestVersion;

    private Thread thread;

    private Integer runningInterval;

    private HttpClient client;

    public synchronized void start() {
        Integer interval = configuredInterval();
        if (interval != null) start(interval);
        else Logger.warn("Update checking is disabled");
    }

    public synchronized void restart() {
        if (thread != null && thread.isAlive() && Objects.equals(configuredInterval(), runningInterval)) return;
        stop();
        start();
    }

    private Integer configuredInterval() {
        if (!PluginConfig.UPDATE_CHECK_ENABLED.get()) return null;
        return PluginConfig.UPDATE_CHECK_INTERVAL.get();
    }

    private void start(int interval) {
        Preconditions.checkState(thread == null);
        runningInterval = interval;
        thread = new Thread(() -> run(interval));
        thread.start();
    }

    public synchronized void stop() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        runningInterval = null;
    }

    private void run(int interval) {
        try {
            while (true) {
                Logger.info("Checking for updates");
                if (!update()) Logger.error("Error getting latest version");
                else {
                    String currentLatestVersion = this.latestVersion;
                    if (!check(currentLatestVersion)) Logger.warn(updateAvailableMessage(currentLatestVersion));
                    else Logger.info(upToDateMessage(currentLatestVersion));
                }
                TimeUnit.HOURS.sleep(interval);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private boolean update() throws InterruptedException {
        try {
            Pattern pattern = Pattern.compile("\"tag_name\":\"v([0-9\\.]+)\"");
            HttpRequest request =
                    HttpRequest.newBuilder().uri(LATEST_RELEASE_URI).GET().build();
            if (client == null) client = HttpClient.newHttpClient();
            String latestVersionData =
                    client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            Matcher matcher = pattern.matcher(latestVersionData);
            if (matcher.find()) {
                latestVersion = matcher.group(1);
                return true;
            }
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean check(String currentLatestVersion) {
        if (currentLatestVersion == null) return true;
        Integer[] installedVersion = castVersionString(getInstalledVersion());
        Integer[] latestVersion = castVersionString(currentLatestVersion);
        for (int i = 0; i < latestVersion.length; i++) {
            int installed = i < installedVersion.length ? installedVersion[i] : 0;
            if (latestVersion[i] > installed) return false;
            if (latestVersion[i] < installed) return true;
        }
        return true;
    }

    private Integer[] castVersionString(String version) {
        return Arrays.stream(version.split("\\.")).map((Integer::parseInt)).toArray(Integer[]::new);
    }

    private String updateAvailableMessage(String currentLatestVersion) {
        return "§eUpdate available! Download at https://modrinth.com/plugin/silkspawners §f\nInstalled version: v"
                + getInstalledVersion()
                + "\nLatest version: v"
                + currentLatestVersion;
    }

    private String upToDateMessage(String currentLatestVersion) {
        return "The plugin is up to date (Current release v" + currentLatestVersion + ")";
    }
}
