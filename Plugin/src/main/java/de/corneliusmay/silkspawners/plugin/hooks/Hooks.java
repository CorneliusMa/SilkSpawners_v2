package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
class Hooks implements Loader {

    private final HookLoader hookLoader;

    private final PluginConfig config;

    @Override
    public boolean load() {
        hookLoader.addHook("shopguiplus.ShopGUIPlusHook", "ShopGUIPlus", config.HOOK_SHOPGUIPLUS);
        hookLoader.register();
        return true;
    }
}
