package de.corneliusmay.silkspawners.plugin.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class UUIDFetcher {

    private final Cache<String, UUID> cache;

    public UUIDFetcher() {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    }

    public UUID fetch(String playerName) {
        UUID uuid = cache.getIfPresent(playerName);
        if(uuid == null) {
            try {
                HttpURLConnection connection = connect(playerName);
                if (connection.getResponseCode() == 200) {
                    uuid = parseUUID(parseJson(connection));
                    cache.put(playerName, uuid);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return uuid;
    }

    public void fetchAsync(String playerName, ExecutorService pool, Consumer<UUID> consumer) {
       pool.execute(() -> consumer.accept(fetch(playerName)));
    }

    private HttpURLConnection connect(String playerName) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        return connection;
    }

    private String parseJson(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        JsonElement element = (new JsonParser()).parse(reader);
        return element.getAsJsonObject().get("id").getAsString();
    }

    private UUID parseUUID(String uuid) {
        return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}
