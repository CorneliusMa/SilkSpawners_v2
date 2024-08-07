package de.corneliusmay.silkspawners.plugin.config;

public enum ConfigScope {
    MESSAGES("messages"),
    SPAWNER("spawner"),
    SPAWNER_ITEM("item", SPAWNER),
    SPAWNER_EXPLOSION("explosion", SPAWNER),
    SPAWNER_MESSAGES("message", SPAWNER),
    SPAWNER_PERMISSIONS("permission", SPAWNER),
    UPDATE("update"),
    UPDATE_CHECK("check", UPDATE),
    ;

    private final String name;

    ConfigScope(String name) {
        this.name = name;
    }

    ConfigScope(String name, ConfigScope parent) {
        this.name = parent.getPath() + name;
    }

    public String getPath() {
        return this.name + ".";
    }
}
