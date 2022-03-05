package stringcalculator.delimiter;

import java.util.regex.Pattern;

public class DefaultDelimiter {

    private final String origin;
    private final Pattern pattern;

    public DefaultDelimiter(String origin, Pattern pattern) {
        this.origin = origin;
        this.pattern = pattern;
    }

    public DefaultDelimiter(String origin) {
        this(origin, Pattern.compile("[,:]"));
    }

    public String[] split() {
        return pattern.split(origin);
    }

}
