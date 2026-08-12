package de.corneliusmay.silkspawners.plugin.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
public class Http {

    private static final String USER_AGENT = "SilkSpawners (github.com/CorneliusMa/SilkSpawners_v2)";

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public HttpRequest.Builder request(URI uri) {
        return HttpRequest.newBuilder(uri).header("User-Agent", USER_AGENT).timeout(REQUEST_TIMEOUT);
    }

    public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> handler)
            throws IOException, InterruptedException {
        return client.send(request, handler);
    }

    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> handler) {
        return client.sendAsync(request, handler);
    }
}
