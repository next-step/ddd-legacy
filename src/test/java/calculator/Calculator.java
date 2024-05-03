package calculator;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Calculator {

    private static final int ZERO = 0;
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String ALPHABET_PATTERN = "[a-zA-Z]";

    public static int calculate(String input) {
        if (isBlank(input)) {
            return ZERO;
        }
        var numbers = toNumbers(input);
        return numbers.sum();
    }

    private static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    private static Numbers toNumbers(String input) {
        return new Numbers(split(input));
    }

    private static String[] split(String input) {
        validate(input);
        Matcher m = DELIMITER_PATTERN.matcher(input);
        if (!m.find()) {
            return input.split("[,:]");
        }

        String customDelimiter = m.group(1);
        return m.group(2).split(customDelimiter);
    }


    private static void validate(String input) {
        if (hasNonNumber(input)) {
            throw new RuntimeException('"' + input + "\" is not a valid number string");
        }
    }

    private static boolean hasNonNumber(String input) {
        return input.matches(ALPHABET_PATTERN);
    }
}
