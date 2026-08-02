package de.corneliusmay.silkspawners.plugin.dump;

import java.util.List;
import java.util.Map;

class DumpJson {

    static String render(Object node) {
        StringBuilder out = new StringBuilder();
        write(out, node, "");
        return out.toString();
    }

    private static void write(StringBuilder out, Object node, String indent) {
        if (node instanceof DumpScope scope) write(out, scope.node(), indent);
        else if (node instanceof Map<?, ?> map) writeObject(out, map, indent);
        else if (node instanceof List<?> list) writeArray(out, list, indent);
        else if (node instanceof Number || node instanceof Boolean) out.append(node);
        else if (node == null) out.append("null");
        else writeString(out, String.valueOf(node));
    }

    private static void writeObject(StringBuilder out, Map<?, ?> map, String indent) {
        if (map.isEmpty()) {
            out.append("{}");
            return;
        }
        String inner = indent + "  ";
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            out.append(first ? "{\n" : ",\n").append(inner);
            first = false;
            writeString(out, String.valueOf(entry.getKey()));
            out.append(": ");
            write(out, entry.getValue(), inner);
        }
        out.append('\n').append(indent).append('}');
    }

    private static void writeArray(StringBuilder out, List<?> list, String indent) {
        if (list.isEmpty()) {
            out.append("[]");
            return;
        }
        String inner = indent + "  ";
        boolean first = true;
        for (Object element : list) {
            out.append(first ? "[\n" : ",\n").append(inner);
            first = false;
            write(out, element, inner);
        }
        out.append('\n').append(indent).append(']');
    }

    private static void writeString(StringBuilder out, String value) {
        out.append('"');
        for (int index = 0; index < value.length(); index++) {
            char c = value.charAt(index);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < ' ') out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
                }
            }
        }
        out.append('"');
    }
}
