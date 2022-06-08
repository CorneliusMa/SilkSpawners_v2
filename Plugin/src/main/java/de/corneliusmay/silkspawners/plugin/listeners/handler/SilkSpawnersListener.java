package de.corneliusmay.silkspawners.plugin.listeners.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.event.Listener;

public abstract class SilkSpawnersListener<T> implements Listener {

    @Setter(AccessLevel.PACKAGE)
    protected SilkSpawners plugin;

    protected abstract void onCall(T event);
}
