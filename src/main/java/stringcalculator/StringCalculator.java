package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private final static Pattern CALCULATOR_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    public static final int CUSTOM_DELIMITER = 1;
    public static final int USER_INPUT = 2;
    public static final String DEFAULT_DELIMITER = "[,:]";
    public static final String CUSTOM_DELIMITER_FORMAT = "[%s]";

    public int add(String text) {
        String[] inputValues = splitAndValidate(text);

        return sum(inputValues);
    }

    private static String[] splitAndValidate(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        String[] userInputValuesForCalculation;
        Matcher m = CALCULATOR_DELIMITER_PATTERN.matcher(text);

        if (m.find()) {
            String customDelimiter = String.format(CUSTOM_DELIMITER_FORMAT
                                                ,m.group(CUSTOM_DELIMITER));
            userInputValuesForCalculation = m.group(USER_INPUT).split(customDelimiter);
        } else {
            userInputValuesForCalculation = text.split(DEFAULT_DELIMITER);
        }
        return userInputValuesForCalculation;
    }

    private static int sum(String[] values) {
        return Arrays.stream(values)
                .mapToInt(val -> PositiveNumber.of(val).number())
                .sum();
    }
}
