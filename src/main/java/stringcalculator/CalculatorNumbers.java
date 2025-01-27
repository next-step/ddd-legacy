package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorNumbers {
    public static final String DEFAULT_DELIMETER = ":|,";
    private static final String CUSTOM_DELIMETER = "//(.)\n(.*)";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMETER);

    private List<Integer> calculatorNumbers;

    public CalculatorNumbers(String operand) {
        Matcher matcher = CUSTOM_PATTERN.matcher(operand);
        String delimeter = DEFAULT_DELIMETER;
        if (matcher.find()) {
            delimeter = matcher.group(1);
            operand = matcher.group(2);
        }
        calculatorNumbers = Arrays.stream(operand.split(delimeter)).map(Integer::parseInt).toList();
    }

    public int sum() {
        return calculatorNumbers.stream().mapToInt(Integer::intValue).sum();
    }


}
