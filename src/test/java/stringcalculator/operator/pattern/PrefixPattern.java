package stringcalculator.operator.pattern;

import java.util.regex.Pattern;

public class PrefixPattern {

    private final String origin;
    private final Pattern pattern;

    public PrefixPattern(String origin, Pattern pattern) {
        this.origin = origin;
        this.pattern = pattern;
    }

    public PrefixPattern(String origin) {
        this(origin, Pattern.compile("//(.)\\\\n"));
    }

    public String removeUnnecessary() {
        return pattern.matcher(origin).replaceAll("");
    }

}
