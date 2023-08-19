package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitCalculatorPolicy implements CalculatorPolicy {

    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int NUMBER_INDEX = 1;
    private Matcher matcher;

    @Override
    public boolean isSupport(String text) {
        matcher = PATTERN.matcher(text);
        return matcher.find();
    }

    @Override
    public int calculate(String text) {
        String customDelimiter = matcher.group(DELIMITER_INDEX);
        return Arrays.stream(matcher.group(NUMBER_INDEX).split(customDelimiter))
                .mapToInt(this::toPositive)
                .sum();
    }
}
