#!/usr/bin/env bash
set -euo pipefail

# next-version.sh <tag-prefix> <ref> <include|exclude> <scope>
# Prints the next version, or nothing when no commit since the last release of
# that artifact warrants one. Commits are routed to an artifact by their
# conventional commit scope, not by the paths they touch, because API changes
# almost always carry Plugin changes along with them. A breaking API change is
# the exception and releases both, since the API ships inside the plugin jar.
# Pre-release tags are ignored when locating the last release.

prefix=$1
ref=$2
match=$3
scope=$4

last=$(git describe --tags --abbrev=0 --match "${prefix}[0-9]*" --exclude '*-beta.*' "${ref}^" 2> /dev/null || true)
range=${last:+${last}..}${ref}

bump=
while IFS= read -r -d '' message; do
    subject=${message%%$'\n'*}
    [[ "$subject" =~ ^([a-z]+)(\(([^\)]*)\))?(!)?: ]] || continue

    breaking=${BASH_REMATCH[4]}
    if [ -z "$breaking" ] && grep -q '^BREAKING[ -]CHANGE:' <<< "$message"; then
        breaking='!'
    fi

    case "$match" in
        include) [ "${BASH_REMATCH[3]}" = "$scope" ] || continue ;;
        exclude) [ "${BASH_REMATCH[3]}" != "$scope" ] || [ -n "$breaking" ] || continue ;;
    esac

    if [ -n "$breaking" ]; then
        bump=major
        break
    fi

    case "${BASH_REMATCH[1]}" in
        feat) bump=minor ;;
        fix | perf) [ -n "$bump" ] || bump=patch ;;
    esac
done < <(git log -z --no-merges --format=%B "$range")

[ -n "$bump" ] || exit 0

IFS=. read -r major minor patch <<< "${last#"$prefix"}"
case "$bump" in
    major) printf '%s.0.0\n' "$((${major:-0} + 1))" ;;
    minor) printf '%s.%s.0\n' "${major:-0}" "$((${minor:-0} + 1))" ;;
    patch) printf '%s.%s.%s\n' "${major:-0}" "${minor:-0}" "$((${patch:-0} + 1))" ;;
esac
