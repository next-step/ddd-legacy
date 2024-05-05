package stringcalculator;

import jakarta.validation.constraints.Pattern;

import java.util.Arrays;

public class StringValidation {

    private final DelimiterParser delimiterParser;

    public StringValidation() {
        this.delimiterParser = new DelimiterParser();
    }

    public boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public boolean checkNegative(String text) {
        return text.matches(ValidationRegex.NEGATIVE_NUM_REGEX.getRegex());

    }

    public int parseNumber(String text) {

        if (isNullOrEmpty(text)) {
            return 0;
        } else if (checkNegative(text)) {
            throw new RuntimeException();
        }


        String[] numbers = delimiterParser.parseDelimiter(text);

        return Arrays.stream(numbers).mapToInt(Integer::parseInt).sum();

    }
}
