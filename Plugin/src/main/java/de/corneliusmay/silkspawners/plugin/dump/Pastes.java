package de.corneliusmay.silkspawners.plugin.dump;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Pastes {

    private static final URI POST_URI = URI.create("https://api.pastes.dev/post");

    private static final String USER_AGENT = "SilkSpawners (github.com/CorneliusMa/SilkSpawners_v2)";

    private static final Pattern KEY_PATTERN = Pattern.compile("\"key\"\\s*:\\s*\"([^\"]+)\"");

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    static CompletableFuture<String> upload(String document) {
        return send(HttpRequest.newBuilder()
                .uri(POST_URI)
                .timeout(TIMEOUT)
                .header("Content-Type", "text/json")
                .header("User-Agent", USER_AGENT)
                .POST(HttpRequest.BodyPublishers.ofString(document))
                .build());
    }

    private static CompletableFuture<String> send(HttpRequest request) {
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(Pastes::pasteUrl);
    }

    private static String pasteUrl(HttpResponse<String> response) {
        if (response.statusCode() / 100 != 2)
            throw new IllegalStateException("Upload failed with status " + response.statusCode());
        Matcher matcher = KEY_PATTERN.matcher(response.body());
        if (!matcher.find()) throw new IllegalStateException("Upload response contained no paste key");
        return "https://pastes.dev/" + matcher.group(1);
    }
}
