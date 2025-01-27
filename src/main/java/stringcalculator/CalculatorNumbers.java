package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorNumbers {
    public static final String DEFAULT_DELIMETER = ":|,";
    private static final String CUSTOM_DELIMETER = "//(.)\n(.*)";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMETER);
    public static final int DELIMETER_INDEX = 1;
    public static final int PURE_OPERAND_INDEX = 2;

    private final List<Integer> calculatorNumbers;

    public CalculatorNumbers(List<Integer> calculatorNumbers) {
        this.calculatorNumbers = calculatorNumbers;
    }

    public static CalculatorNumbers createNumbers(String operand) {
        return new CalculatorNumbers(convertOperandToNumbers(operand));
    }

    private static List<Integer> convertOperandToNumbers(String operand) {
        Matcher matcher = CUSTOM_PATTERN.matcher(operand);
        String delimeter = DEFAULT_DELIMETER;
        if (matcher.find()) {
            delimeter = matcher.group(DELIMETER_INDEX);
            operand = matcher.group(PURE_OPERAND_INDEX);
        }
        return Arrays.stream(operand.split(delimeter))
                .map(Integer::parseInt)
                .toList();
    }

    public int sum() {
        return calculatorNumbers.stream().
                mapToInt(Integer::intValue)
                .sum();
    }
}
