package de.corneliusmay.silkspawners.plugin.utils;

public class StringUtils {
    public static String capitalizeFully(String string) {
        if(string == null || string.isEmpty()) return string;
        String[] words = string.split(" ");
        StringBuilder sb = new StringBuilder();
        for(String word : words) {
            sb.append(capitalize(word)).append(" ");
        }
        return sb.toString().trim();
    }

    public static String capitalize(String string) {
        if(string == null || string.isEmpty()) return string;
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
