package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VersionChecker {

    private boolean running;

    @Getter
    private String latestVersion;
    private final ExecutorService pool;

    private final HttpClient client;

    public VersionChecker() {
        pool = Executors.newFixedThreadPool(1);
        client = HttpClient.newHttpClient();
    }

    public void start(int interval) {
        if(running) return;
        running = true;

        pool.execute(() -> run(interval));
    }

    public void stop() {
        running = false;
        pool.shutdownNow();
    }

    private void run(int interval) {
        while(running) {
            try {
                SilkSpawners.getInstance().getLog().info("Checking for updates");
                if(!updateLatestVersion()) SilkSpawners.getInstance().getLog().error("Error getting latest version");
                else if(!check()) SilkSpawners.getInstance().getLog().warn("§eUpdate available! Download at https://www.spigotmc.org/resources/silkspawners.60063/ §f\nInstalled version: v" + getInstalledVersion() + "\nLatest version: v" + latestVersion);
                else SilkSpawners.getInstance().getLog().info("The plugin is up to date (v" + latestVersion + ")");

                TimeUnit.HOURS.sleep(interval);
            } catch (InterruptedException ignored) {}
        }
    }

    private boolean updateLatestVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://storage.googleapis.com/silkspawners/version.html")).GET().build();
            latestVersion = client.send(request, HttpResponse.BodyHandlers.ofString()).body().replace("\r", ""). replace("\n", "");
            return true;
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean check() {
        return latestVersion.equals(getInstalledVersion());
    }

    public static String getInstalledVersion() {
        return SilkSpawners.getInstance().getDescription().getVersion();
    }
}
