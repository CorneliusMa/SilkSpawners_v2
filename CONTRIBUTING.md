# Contributing

Thank you for being interested in contributing to SilkSpawners. Please make sure to read the guidelines
below.

## Translations

We will not accept pull requests adding translations. Please use our Crowdin [translation program](https://crowdin.com/project/silkspawners).

## Pull Requests

We will often add small changes to your pull request directly before merging it. These changes may range from formatting, slight refactoring where necessary to more advanced additions.

**Make sure to use personal forks (do not use an organization).**

## Code style

Java code is formatted with [Palantir Java Format](https://github.com/palantir/palantir-java-format) enforced through [Spotless](https://github.com/diffplug/spotless).
The build fails on unformatted code, so format your changes with

```shell
./gradlew spotlessApply
```

before committing - or enable the git hooks below to have this happen automatically.

## Git hooks

The repository ships optional git hooks in `.githooks/` that format staged Java files with Spotless before each commit and validate commit messages against the Conventional Commits format.
Enable them once per clone with:

```shell
git config core.hooksPath .githooks
```

## Commits

We like to follow the [Conventional Commits](https://www.conventionalcommits.org) specification. This allows us to automatically generate neat changelogs, for example.

Your commit messages should be structured as follows:
```
<type>[optional scope]: <description>
# OR, FOR BREAKING CHANGES
<type>[optional scope]!: <description>

[optional body]

[optional footer(s)]
```

Where `type` can be any of the following:

* **`build`**: Changes that affect the build system or external dependencies (example scopes: npm, gradle, maven)
* **`ci`**: Changes to our CI configuration files and scripts (example scopes: Actions, Travis, Circle)
* **`docs`**: Documentation only changes
* **`feat`**: A new feature
* **`fix`**: A bug fix
* **`perf`**: A code change that improves performance
* **`refactor`**: A code change that neither fixes a bug nor adds a feature
* **`style`**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
* **`test`**: Adding missing tests or correcting existing tests
* **`revert`**: Explicitly reverting commit(s)
* **`chore`**: Other changes that don't modify source or test files

### Examples

```
fix(modules): load crash due to thread unsafety
```

```
build: change maven artifactId
```

For more just have a look at our commit history.

## Adding support for a new Minecraft version

Version-specific code is isolated in per-version modules (`v1_8_4`, `v1_9_4`, …). Each module contains a single `BukkitHandler` that implements the [`Bukkit`](SPI/src/main/java/de/corneliusmay/silkspawners/spi/version/Bukkit.java) API interface, and the correct handler is picked at runtime by `MinecraftVersionChecker`.

**You only need a new module when the Bukkit API changes in a way that breaks the existing handler** - not for every Minecraft release. As long as the current handler keeps compiling and working against a newer server, nothing has to be done.

When a new module *is* required:

1. Copy an existing module (e.g. `v1_21_3`) to `vX_Y_Z`. The module name marks the lowest server version its handler applies to.
2. In its `build.gradle.kts`, bump the `compileOnly` `spigot-api` dependency to the version you are targeting.
3. Update the `BukkitHandler` (package `de.corneliusmay.silkspawners.bukkit.vX_Y_Z`) so it implements every method of the `Bukkit` interface against the new API.
4. Register the module in [`settings.gradle.kts`](settings.gradle.kts). That is the only build change needed: the Plugin automatically compiles every registered module into the jar (and excludes it from jar minimization), except the non-core modules listed in [`Plugin/build.gradle.kts`](Plugin/build.gradle.kts).
5. Add a branch to [`MinecraftVersionChecker.getBukkitVersion()`](Plugin/src/main/java/de/corneliusmay/silkspawners/plugin/version/MinecraftVersionChecker.java) returning `"vX_Y_Z"`. Keep the checks ordered from newest to oldest, since the first matching `versionIsNewerOrEqualTo(...)` wins.

There is no automated test for version handlers, so verify your changes on a real server running the target version.

## Adding a plugin hook

Integrations with other plugins live in their own `HookXxx` modules and are loaded through `HookLoader`, which only activates a hook when the target plugin is installed *and* the corresponding config option is enabled.

A hook extends the [`Hook`](SPI/src/main/java/de/corneliusmay/silkspawners/spi/hooks/Hook.java) base class, which provides the `plugin` instance and a [`SpawnerProvider`](SPI/src/main/java/de/corneliusmay/silkspawners/spi/hooks/SpawnerProvider.java) (to build a SilkSpawners spawner item for a given `EntityType`, and to read the `EntityType` back out of a spawner item), and requires a `register()` method. External plugins integrate through the developer API instead, see the [Developer documentation](docs/DEVELOPERS.md).

To add one:

1. Create a `HookXxx` module. Its `build.gradle.kts` should apply the `silkspawners.core-module` convention plugin (which provides the `:SPI` dependency) and `compileOnly` the target plugin's API. If the API needs an extra repository, add it to the `dependencyResolutionManagement` block in [`settings.gradle.kts`](settings.gradle.kts) - repositories are declared centrally there, not per module.
2. Implement a class extending `Hook` in package `de.corneliusmay.silkspawners.hooks.<subpackage>`, doing the integration work inside `register()`. If your hook listens to Bukkit events, register it as a `Listener` there yourself - the loader only calls `register()`.
3. Register the module in [`settings.gradle.kts`](settings.gradle.kts); the Plugin picks up every registered module automatically, so no dependency declaration is needed.
4. Add a toggle for it in `PluginConfig` under the existing `HOOKS` scope, giving it a boolean default (e.g. `HOOK_XXX(builder(HOOKS, "xxx").defs(true).formatter(new BooleanConfigValue()))`).
5. Wire it up in `SilkSpawners.registerHooks()`:
   ```java
   hookLoader.addHook("<subpackage>.<ClassName>", "<TargetPluginName>", PluginConfig.HOOK_XXX);
   ```
