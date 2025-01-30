package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorNumbers {
    private static final String DEFAULT_DELIMITER = ":|,";
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMITER);
    private static final int DELIMITER_INDEX = 1;
    private static final int PURE_OPERAND_INDEX = 2;

    private final List<Integer> calculatorNumbers;

    public CalculatorNumbers(List<Integer> calculatorNumbers) {
        this.calculatorNumbers = calculatorNumbers;
    }

    public static CalculatorNumbers createNumbers(String operand) {
        return new CalculatorNumbers(convertOperandToNumbers(operand));
    }

    private static List<Integer> convertOperandToNumbers(String operand) {
        Matcher matcher = CUSTOM_PATTERN.matcher(operand);
        String delimeter = DEFAULT_DELIMITER;
        if (matcher.find()) {
            delimeter = matcher.group(DELIMITER_INDEX);
            operand = matcher.group(PURE_OPERAND_INDEX);
        }
        return Arrays.stream(operand.split(delimeter))
                .map(Integer::parseInt)
                .toList();
    }

    public int sum() {
        return calculatorNumbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
