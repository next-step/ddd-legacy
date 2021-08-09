package step1.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Number {

    public final static String NUMBER_PATTERN = "^[0-9]";
    private final static Pattern numberPattern = Pattern.compile(NUMBER_PATTERN);
    private final int value;

    public Number(String value) {
        Matcher numberMatcher = numberPattern.matcher(value);
        if (!numberMatcher.find()) {
            throw new RuntimeException();
        }
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return this.value;
    }

}
