package string_additional_calculator;

import java.util.regex.Pattern;

class StringAdditionalCalculator {
    private final Pattern defaultSeparatePattern = Pattern.compile("[,:]");

    public int calculate(String expression) {
        String[] numbers = extractNumbers(expression);
        int result = 0;
        for (String number : numbers) {
            result += Integer.parseInt(number);
        }
        return result;
    }

    private String[] extractNumbers(String expression) {
        return defaultSeparatePattern.split(expression);
    }
}