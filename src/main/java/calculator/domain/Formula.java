package calculator.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int NUMBER_INPUT_INDEX = 2;
    private static final String EMPTY_DELIMITER = "";
    private static final String[] EMPTY_NUMBERS = new String[0];

    private final String delimiter;
    private final Numbers numbers;
    private final int result;

    public Formula(String input) {
        this.delimiter = getDelimiter(input);
        this.numbers = new Numbers(getNumbersByDelimiter(input, delimiter));
        this.result = sum();
    }

    private String[] getNumbersByDelimiter(String input, String delimiter) {
        if (input == null || input.isBlank()) {
            return EMPTY_NUMBERS;
        }

        Matcher matched = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matched.find()) {
            return matched.group(NUMBER_INPUT_INDEX).split(delimiter);
        }

        return input.split(DEFAULT_DELIMITER);
    }

    private String getDelimiter(String input) {
        if (input == null || input.isBlank()) {
            return EMPTY_DELIMITER;
        }
        
        Matcher matched = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matched.find()) {
            return matched.group(DELIMITER_INDEX);
        }

        return DEFAULT_DELIMITER;
    }

    private int sum() {
        return numbers.sum();
    }

    public int getResult() {
        return result;
    }
}
