package net.snackbag.tabmanager.util;

import java.util.regex.Pattern;

public class RegexCompiler {

    /**
     * Compiles simple filter expressions like minecraft:*_wool or *:some_block
     * @param pattern The pattern in form of a string (e.g. minecraft:*_wool or *:some_block)
     * @return The regular expression {@link Pattern}
     */
    public static Pattern compileGlob(String pattern) {
        // Build regex by escaping every character except '*' which we treat as wildcard
        StringBuilder sb = new StringBuilder();
        for (char c : pattern.toCharArray()) {
            if (c == '*') {
                sb.append(".*");
            } else {
                sb.append(Pattern.quote(String.valueOf(c)));
            }
        }
        return Pattern.compile("^" + sb + "$");
    }

    /**
     * Compiles a regular expression pattern from a string like
     * @param pattern The regex pattern in form of a string
     * @return The regular expression {@link Pattern}
     */
    public static Pattern compileRegex(String pattern) {
        return Pattern.compile(pattern);
    }

}
