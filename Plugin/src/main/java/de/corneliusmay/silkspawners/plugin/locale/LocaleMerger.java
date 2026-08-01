package de.corneliusmay.silkspawners.plugin.locale;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

final class LocaleMerger {

    record Result(List<String> lines, int added, int updated, int kept) {}

    private record Message(String key, String value) {}

    private record Entry(List<String> preamble, String line, String value) {}

    private record Document(Map<String, Entry> entries, List<String> trailing) {}

    private final Map<String, Set<String>> signatures;

    private int added;

    private int updated;

    private int kept;

    LocaleMerger(Map<String, Set<String>> signatures) {
        this.signatures = signatures;
    }

    static Map<String, Map<String, Set<String>>> signatures(List<String> source) {
        Map<String, Map<String, Set<String>>> signatures = new LinkedHashMap<>();
        for (String line : source) {
            Message message = message(line);
            int locale = message == null ? -1 : message.key().indexOf('/');
            if (locale < 0) continue;
            signatures
                    .computeIfAbsent(message.key().substring(0, locale), ignored -> new LinkedHashMap<>())
                    .put(
                            message.key().substring(locale + 1),
                            Set.of(message.value().split(",")));
        }
        return signatures;
    }

    static List<String> lines(String text) {
        List<String> folded = new ArrayList<>();
        StringBuilder logical = new StringBuilder();
        for (String line : text.lines().toList()) {
            boolean start = logical.isEmpty();
            if (!start) logical.append('\n');
            logical.append(line);
            if (continues(line) && !(start && key(line) == null)) continue;
            folded.add(logical.toString());
            logical.setLength(0);
        }
        if (!logical.isEmpty()) folded.add(logical.toString());
        return folded;
    }

    Result merge(List<String> bundled, List<String> current) {
        added = 0;
        updated = 0;
        kept = 0;
        Document ours = parse(current);
        List<String> output = new ArrayList<>(bundled.size());
        List<String> preamble = new ArrayList<>();
        for (String line : bundled) {
            Message message = message(line);
            if (message == null) {
                preamble.add(line);
                continue;
            }
            Entry entry = ours.entries().remove(message.key());
            output.addAll(annotated(preamble) || entry == null ? preamble : entry.preamble());
            preamble.clear();
            output.add(resolve(line, message, entry));
        }
        output.addAll(preamble);
        append(output, orphans(ours));
        return new Result(output, added, updated, kept);
    }

    private String resolve(String bundled, Message message, Entry entry) {
        if (entry == null) {
            added++;
            return bundled;
        }

        if (entry.value().equals(message.value())) return bundled;
        if (!written(message.key(), entry.value())) {
            kept++;
            return entry.line();
        }
        updated++;
        return bundled;
    }

    private List<String> orphans(Document ours) {
        List<String> orphans = new ArrayList<>();
        ours.entries().forEach((key, entry) -> {
            if (written(key, entry.value())) return;
            orphans.addAll(entry.preamble());
            orphans.add(entry.line());
            kept++;
        });
        if (annotated(ours.trailing())) orphans.addAll(ours.trailing());
        return orphans;
    }

    private void append(List<String> output, List<String> orphans) {
        if (orphans.isEmpty()) return;
        if (!output.isEmpty()
                && !output.get(output.size() - 1).isBlank()
                && !orphans.get(0).isBlank()) output.add("");
        output.addAll(orphans);
    }

    private static boolean annotated(List<String> preamble) {
        return preamble.stream().anyMatch(line -> !line.isBlank());
    }

    private boolean written(String key, String value) {
        Set<String> known = signatures.get(key);
        return known != null && known.contains(hash(value));
    }

    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, 12);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static Document parse(List<String> source) {
        Map<String, Entry> entries = new LinkedHashMap<>();
        List<String> preamble = new ArrayList<>();
        for (String line : source) {
            Message message = message(line);
            if (message == null) {
                preamble.add(line);
                continue;
            }
            entries.put(message.key(), new Entry(List.copyOf(preamble), line, message.value()));
            preamble.clear();
        }
        return new Document(entries, List.copyOf(preamble));
    }

    static String key(String line) {
        Message message = message(line);
        return message == null ? null : message.key();
    }

    private static Message message(String line) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(line));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return properties.stringPropertyNames().stream()
                .findFirst()
                .map(key -> new Message(key, properties.getProperty(key)))
                .orElse(null);
    }

    private static boolean continues(String line) {
        int backslashes = 0;
        for (int i = line.length() - 1; i >= 0 && line.charAt(i) == '\\'; i--) backslashes++;
        return backslashes % 2 == 1;
    }
}
