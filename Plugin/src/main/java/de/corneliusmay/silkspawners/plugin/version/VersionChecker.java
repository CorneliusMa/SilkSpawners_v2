package de.corneliusmay.silkspawners.plugin.version;

import com.google.common.base.Preconditions;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChecker {

    private final SilkSpawners plugin;
    private final HttpClient client;

    @Getter
    private String latestVersion;

    private Thread thread;

    public VersionChecker(SilkSpawners plugin) {
        this.plugin = plugin;
        client = HttpClient.newHttpClient();
    }

    public void start(int interval) {
        Preconditions.checkState(thread == null);
        thread = new Thread(() -> run(interval));
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    private void run(int interval) {
        try {
            while (true) {
                plugin.getLog().info("Checking for updates");
                if (!update()) plugin.getLog().error("Error getting latest version");
                else if (!check())
                    plugin.getLog().warn("§eUpdate available! Download at https://www.spigotmc.org/resources/silkspawners.60063/ §f\nInstalled version: v" + getInstalledVersion() + "\nLatest version: v" + latestVersion);
                else plugin.getLog().info("The plugin is up to date (Current release v" + latestVersion + ")");
                TimeUnit.HOURS.sleep(interval);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private boolean update() throws InterruptedException {
        try {
            Pattern pattern = Pattern.compile("\"tag_name\":\"v([0-9\\.]+)\"");
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.github.com/repos/CorneliusMa/SilkSpawners_v2/releases/latest")).GET().build();
            String latestVersionData = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
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

    public boolean check() {
        Integer[] installedVersion = castVersionString(getInstalledVersion());
        Integer[] latestVersion = castVersionString(this.latestVersion);
        for (int i = 0; i < latestVersion.length; i++) {
            if (i >= installedVersion.length) return true;
            if (latestVersion[i] > installedVersion[i]) return false;
        }
        return true;
    }

    public String getInstalledVersion() {
        return plugin.getDescription().getVersion();
    }

    private Integer[] castVersionString(String version) {
        return Arrays.stream(version.split("\\.")).map((Integer::parseInt)).toArray(Integer[]::new);
    }
}
