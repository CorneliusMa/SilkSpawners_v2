# Contributing

Thank you for being interested in contributing to SilkSpawners. Please make sure to read the guidelines
below.

## Translations

We will not accept pull requests adding translations. Please use our Crowdin [translation program](https://crowdin.com/project/silkspawners).

Only `messages_en.properties` may be edited in this repository. It is the Crowdin source file, and every other
locale file is downloaded from Crowdin by a nightly sync and opened as a pull request. Local changes to a translated
file are never uploaded, so they are overwritten and lost on the next sync. Add or reword messages in English only.

### Editing messages locally

Locale files in a server's `plugins/SilkSpawners_v2/locale/` folder are merged on startup: new and reworded messages
from the jar are applied, while messages the server owner edited are recognised as customizations and left alone.
A message counts as plugin-written only if its current text matches the jar, or if it matches a wording the plugin
shipped in a released version.

That means an unreleased wording is indistinguishable from a customization. When you change a message and rebuild,
the first change reaches your test server, because it replaces the released wording. Every further change to the
same message does not, because your server now holds a wording that was never released, so the merge protects it.

If a message change does not show up on your test server, that is why. Run `/silkspawners locale restore confirm`
or delete the `locale` folder to pick it up.

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

### The api scope

Scopes are free-form, except `api`, which releases the `API` module. For `feat`, `fix` and `perf` the hook and CI enforce that scope and files agree:

* Changing `API/` requires the `api` scope
* Using the `api` scope requires changing `API/`
* A breaking `api` change releases the plugin too, the API ships inside its jar

### Examples

```
fix(modules): load crash due to thread unsafety
```

```
build: change maven artifactId
```

For more just have a look at our commit history.

## Releases

Versions are never edited by hand.

* Each push to `master` recomputes the next version from the commits since the last release and opens or updates the `release/next` pull request
* Merging it tags `v<version>` (plus `api-<version>`) and publishes to GitHub, Hangar and Modrinth
* The branch is force updated on every push, so never commit to it
* Text above the `<!-- commits -->` marker in the pull request body becomes the release notes and survives regeneration, the list below is refreshed. Empty means commit subjects are used

Pre-releases: run the `Publish` workflow on `release/next`, the only branch it accepts.

* Labelled `<next version>-beta.<commits since the last release>`
* Published to the Hangar `Preview` and Modrinth `beta` channels
* Tagged on the `master` commit built from, so they sort before the release

## Component wiring

The plugin is wired with [weftkit](https://weftkit.org) - see [Components](https://weftkit.org/components/), [Lifecycle](https://weftkit.org/lifecycle/) and [Annotations](https://weftkit.org/annotations/). When adding a component, declare exactly the dependencies it uses as constructor parameters; `SilkSpawners` is only the composition root and holds no accessors to pass around.

## Adding support for a new Minecraft version

Version-specific code is isolated in per-version modules under [`versions/`](versions) (`v1_8`, `v1_9_4`, …). Each module contains a single `VersionImplementation` that implements the [`VersionAdapter`](SPI/src/main/java/de/corneliusmay/silkspawners/spi/version/VersionAdapter.java) API interface, and the correct implementation is picked at runtime by `MinecraftVersionChecker`. Mechanics that changed at some point in Bukkit history are not duplicated per version - they live in shared modules under [`versions/shared/`](versions/shared), one module per side of the cutoff. The spawner-tag methods come from a storage base class the implementation extends: `PDCVersionAdapter` (for servers since 1.16.5) or `NBTVersionAdapter` (reflection over versioned NMS packages for older servers). The other mechanics come from mixin interfaces with default methods: `SpawnerSettingsVersionAdapter` (since 1.12; older servers compose `MobSpawnerFields` from SpawnerSettingsLegacy instead), `SpawnEggVersionAdapter` (since 1.13) or `SpawnEggLegacyVersionAdapter` (before), and `DualWieldVersionAdapter` (since 1.9; `v1_8` implements the single-hand method itself). Every version module applies the `silkspawners.version-module` convention plugin, which selects the matching shared modules and server artifact from the declared API version, so the build file never lists them itself.

**You only need a new module when the Bukkit API changes in a way that breaks the existing implementation** - not for every Minecraft release. As long as the current implementation keeps compiling and working against a newer server, nothing has to be done.

When a new module *is* required:

1. Copy an existing module (e.g. `versions/v1_21_3`) to `versions/vX_Y_Z`. The module name marks the lowest server version its implementation applies to. New versions always extend `PDCVersionAdapter` and implement the `SpawnerSettingsVersionAdapter`, `SpawnEggVersionAdapter` and `DualWieldVersionAdapter` mixins; the legacy counterparts only exist for servers predating those APIs.
2. In its `build.gradle.kts`, declare the API version you are targeting: `versionModule { spigotApi("X.Y.Z") }`. The convention plugin derives the `spigot-api` dependency and the support modules from it.
3. Update the `VersionImplementation` (package `de.corneliusmay.silkspawners.bukkit.vX_Y_Z`) so it implements the remaining methods of the `VersionAdapter` interface against the new API.
4. Register the module as `versions:vX_Y_Z` in [`settings.gradle.kts`](settings.gradle.kts). That is the only build change needed: the Plugin automatically compiles every registered module into the jar (and excludes it from jar minimization), except the non-core modules listed in [`Plugin/build.gradle.kts`](Plugin/build.gradle.kts).
5. Add a `new Baseline(X, Y, Z)` entry to `SUPPORTED_BASELINES` in [`MinecraftVersionChecker`](Plugin/src/main/java/de/corneliusmay/silkspawners/plugin/version/MinecraftVersionChecker.java). The `vX_Y_Z` module name is derived from the baseline (a trailing `.0` patch is dropped, e.g. `1.16.0` → `v1_16`), so it always matches the module you registered. The highest baseline the server satisfies wins.

There is no automated test for version handlers, so verify your changes on a real server running the target version.

## Adding a section to the dump

`/silkspawners dump` renders a JSON report from a list of [`Dumpable`](Plugin/src/main/java/de/corneliusmay/silkspawners/plugin/dump/Dumpable.java) sections and creates it on [pastes.dev](https://pastes.dev), falling back to a file in the plugin folder when that fails. A section describes itself into the writer it is handed:

```java
writer.section("my-subsystem").value("implementation", implementation.getClass().getName());
```

A section that throws is replaced by an error entry instead of failing the dump, so a broken subsystem is still reported.

Components that already exist implement `Dumpable` themselves (`ConfigLoader`, `HookLoader`, …); state that belongs to no component gets a stateless class in [`dump/sections`](Plugin/src/main/java/de/corneliusmay/silkspawners/plugin/dump/sections). Either way, add it to the list in [`Dump`](Plugin/src/main/java/de/corneliusmay/silkspawners/plugin/dump/Dump.java) - the order there is the order in the report. Never put player data, IP addresses or anything else identifying into a dump: reports are public once created and get pasted into issues.

## Adding a plugin hook

Integrations with other plugins live in their own `HookXxx` modules under [`hooks/`](hooks) and are loaded through `HookLoader`, which only activates a hook when the target plugin is installed *and* the corresponding config option is enabled.

A hook extends the [`Hook`](SPI/src/main/java/de/corneliusmay/silkspawners/spi/hooks/Hook.java) base class, which provides the `plugin` instance and a [`SpawnerProvider`](SPI/src/main/java/de/corneliusmay/silkspawners/spi/hooks/SpawnerProvider.java) (to build a SilkSpawners spawner item for a given `EntityType`, and to read the `EntityType` back out of a spawner item), and requires a `register()` method. External plugins integrate through the developer API instead, see the [Developer documentation](docs/DEVELOPERS.md).

To add one:

1. Create a `hooks/HookXxx` module. Its `build.gradle.kts` should apply the `silkspawners.core-module` convention plugin (which provides the `:SPI` dependency) and `compileOnly` the target plugin's API. If the API needs an extra repository, add it to the `dependencyResolutionManagement` block in [`settings.gradle.kts`](settings.gradle.kts) - repositories are declared centrally there, not per module.
2. Implement a class extending `Hook` in package `de.corneliusmay.silkspawners.hooks.<subpackage>`, doing the integration work inside `register()`. If your hook listens to Bukkit events, register it as a `Listener` there yourself - the loader only calls `register()`.
3. Register the module as `hooks:HookXxx` in [`settings.gradle.kts`](settings.gradle.kts); the Plugin picks up every registered module automatically, so no dependency declaration is needed.
4. Add a toggle for it in `PluginConfig` under the existing `HOOKS` scope, giving it a boolean default (e.g. `HOOK_XXX(builder(HOOKS, "xxx").defs(true).formatter(new BooleanConfigValue()))`).
5. Wire it up in `SilkSpawners.registerHooks()`:
   ```java
   hookLoader.addHook("<subpackage>.<ClassName>", "<TargetPluginName>", PluginConfig.HOOK_XXX);
   ```
