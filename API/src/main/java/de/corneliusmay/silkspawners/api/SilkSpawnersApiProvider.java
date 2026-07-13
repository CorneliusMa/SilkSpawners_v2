package de.corneliusmay.silkspawners.api;

import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Static access to the {@link SilkSpawnersAPI} service. Available once SilkSpawners is enabled;
 * depend (or softdepend) on {@code SilkSpawners_v2} to guarantee load order.
 */
public final class SilkSpawnersApiProvider {

    private SilkSpawnersApiProvider() {}

    /**
     * @return the API instance
     * @throws IllegalStateException if SilkSpawners is not enabled
     */
    public static SilkSpawnersAPI get() {
        return find().orElseThrow(() ->
                new IllegalStateException("SilkSpawners API is not available. Is the SilkSpawners plugin enabled?"));
    }

    /**
     * @return the API instance, or empty if SilkSpawners is not enabled
     */
    public static Optional<SilkSpawnersAPI> find() {
        RegisteredServiceProvider<SilkSpawnersAPI> registration =
                Bukkit.getServicesManager().getRegistration(SilkSpawnersAPI.class);
        return registration == null ? Optional.empty() : Optional.of(registration.getProvider());
    }
}
