package net.snackbag.tabmanager.util;

import java.util.regex.Pattern;

public class RegexCompiler {

    /**
     * Compiles simple filter expressions like minecraft:*_wool or *:some_block
     * @param pattern The pattern in form of a string (e.g. minecraft:*_wool or *:some_block)
     * @return The regular expression {@link Pattern}
     */
    public static Pattern compileGlob(String pattern) {
        String regex = Pattern.quote(pattern).replace("\\*", ".*");
        return Pattern.compile("^" + regex + "$");
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
