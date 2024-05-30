package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Calculator {

    private static final String DELIMITER_REGEX = ",|:";
    private static final int CUSTOM_DELIMITER = 1;
    private static final int INPUT_STRING = 2;
    private static final String CALCULATOR_REGEX = "//(.)\n(.*)";
    private static final Pattern pattern = Pattern.compile(CALCULATOR_REGEX);

    public static Long splitAndSum(String input) {
        if (isNullOrBlank(input)) {
            return 0L;
        }

        Matcher m = Pattern.compile(CALCULATOR_REGEX).matcher(input);
        if (m.find()) {
            String delimiter = m.group(CUSTOM_DELIMITER);
            String[] splitNumbers = m.group(INPUT_STRING).split(delimiter);
            return sumNumbers(splitNumbers);
        }

        String[] splitNumbers = input.split(DELIMITER_REGEX);
        return sumNumbers(splitNumbers);
    }

    private static boolean isNullOrBlank(String input) {
        return input == null || input.isBlank();
    }

    private static Long sumNumbers(String[] numbers) {
        return Arrays.stream(numbers)
                .map(Operand::new)
                .mapToLong(Operand::getValue)
                .sum();
    }
}
