package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorInput {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = ",:";

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX);

    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int STRING_TO_SPLIT_INDEX = 2;

    private final String calculatorInput;

    public CalculatorInput(String calculatorInput) {
        this.calculatorInput = calculatorInput;
    }

    public String[] parse() {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(calculatorInput);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
            return m.group(STRING_TO_SPLIT_INDEX).split("[" + DEFAULT_DELIMITER + customDelimiter + "]");
        }

        return calculatorInput.split("[" + DEFAULT_DELIMITER + "]");
    }
}
