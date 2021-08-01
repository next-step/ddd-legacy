package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seperator {

    static final String DEFAULT_DELIMITER = ",|:";
    static final String SEPARATOR = "|";
    static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    static final int ONE = 1;
    static final int TWO = 2;

    private final String targetNumber;
    private final String delimiter;

    private Seperator(final String targetNumber, final String delimiter) {
        this.targetNumber = targetNumber;
        this.delimiter = delimiter;
    }

    public static Seperator of(final String stringNumber) {
        final Matcher matcher = pattern.matcher(stringNumber);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(ONE);
            final String targetNumber = matcher.group(TWO);
            return new Seperator(targetNumber, DEFAULT_DELIMITER + SEPARATOR + customDelimiter);
        }
        return new Seperator(stringNumber, DEFAULT_DELIMITER);
    }

    public String getTargetNumber() {
        return targetNumber;
    }

    public String getDelimiter() {
        return delimiter;
    }
}
