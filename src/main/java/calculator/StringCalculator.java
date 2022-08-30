package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final String DELIMITER_REGEX = "//(.)\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(DELIMITER_REGEX);
    private static final int DELIMITER_MATCHER_GROUP = 1;
    private static final int INPUT_NUMBER_MATCHER_GROUP = 2;

    public final int add(String input) {

        if (!validateInput(input)) {
            return 0;
        }

        String delimiter = DEFAULT_DELIMITER;
        final Matcher matcher = PATTERN.matcher(input);

        if (matcher.find()) {
            delimiter = matcher.group(DELIMITER_MATCHER_GROUP);
            input = matcher.group(INPUT_NUMBER_MATCHER_GROUP);
        }

        return sum(split(input, delimiter));
    }

    private boolean validateInput(String input) {
        return input != null && !input.isEmpty();
    }

    private String[] split(String input, String delimiter) {
        return input.split(delimiter);
    }

    private int sum(String[] sumStrings) {
        int calculatorValue = 0;
        for (String number : sumStrings) {
            PositiveNumber positiveNumber = new PositiveNumber(number);
            calculatorValue = positiveNumber.add(calculatorValue);
        }
        return calculatorValue;
    }

}
