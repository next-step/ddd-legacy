package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorInput {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = ",:";

    private final String calculatorInput;

    public CalculatorInput(String calculatorInput) {
        this.calculatorInput = calculatorInput;
    }

    public String[] parse() {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(calculatorInput);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split("[" + DEFAULT_DELIMITER + customDelimiter + "]");
        }

        return calculatorInput.split("[" + DEFAULT_DELIMITER + "]");
    }
}
