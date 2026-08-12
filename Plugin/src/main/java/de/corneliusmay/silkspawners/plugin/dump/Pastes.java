package de.corneliusmay.silkspawners.plugin.dump;

import de.corneliusmay.silkspawners.plugin.utils.Http;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Pastes {

    private static final URI POST_URI = URI.create("https://api.pastes.dev/post");

    private static final Pattern KEY_PATTERN = Pattern.compile("\"key\"\\s*:\\s*\"([^\"]+)\"");

    private final Http http;

    CompletableFuture<String> upload(String document) {
        return send(http.request(POST_URI)
                .header("Content-Type", "text/json")
                .POST(HttpRequest.BodyPublishers.ofString(document))
                .build());
    }

    private CompletableFuture<String> send(HttpRequest request) {
        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(Pastes::pasteUrl);
    }

    private static String pasteUrl(HttpResponse<String> response) {
        if (response.statusCode() / 100 != 2)
            throw new IllegalStateException("Upload failed with status " + response.statusCode());
        Matcher matcher = KEY_PATTERN.matcher(response.body());
        if (!matcher.find()) throw new IllegalStateException("Upload response contained no paste key");
        return "https://pastes.dev/" + matcher.group(1);
    }
}
