package calculator.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int NUMBER_INPUT_INDEX = 2;

    private final String delimiter;
    private final Numbers numbers;

    public static Formula of(String input) {
        String delimiter = getDelimiter(input);
        Numbers numbers = Numbers.of(getNumbersByDelimiter(input, delimiter));
        return new Formula(delimiter, numbers);
    }

    private Formula(String delimiter, Numbers numbers) {
        this.delimiter = delimiter;
        this.numbers = numbers;
    }

    private static String[] getNumbersByDelimiter(String input, String delimiter) {
        Matcher matched = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matched.find()) {
            return matched.group(NUMBER_INPUT_INDEX).split(delimiter);
        }

        return input.split(DEFAULT_DELIMITER);
    }

    private static String getDelimiter(String input) {
        Matcher matched = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matched.find()) {
            return matched.group(DELIMITER_INDEX);
        }

        return DEFAULT_DELIMITER;
    }

    public int sum() {
        return numbers.sum();
    }
}
