# Contributing

Thank you for being interested in contributing to SilkSpawners. Please make sure to read the guidelines
below.

## Translations

We will not accept pull requests adding translations. Please use our Crowdin [translation program](https://crowdin.com/project/silkspawners).

## Pull Requests

We will often add small changes to your pull request directly before merging it. These changes may range from formatting, slight refactoring where necessary to more advanced additions.

**Make sure to use personal forks (do not use an organization).**

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
