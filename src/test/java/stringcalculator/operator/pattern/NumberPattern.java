package stringcalculator.operator.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberPattern {

    private final String origin;
    private final Pattern pattern;

    public NumberPattern(String origin, Pattern pattern) {
        this.origin = origin;
        this.pattern = pattern;
    }

    public NumberPattern(String origin) {
        this(origin, Pattern.compile("\\d+"));
    }

    public Matcher getMatcher() {
        return pattern.matcher(origin);
    }

}
