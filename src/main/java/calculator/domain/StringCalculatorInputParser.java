package calculator.domain;

import java.util.Arrays;
import java.util.List;

public class StringCalculatorInputParser {
    private final StringCalculatorDelimiters delimiters;

    public StringCalculatorInputParser(StringCalculatorDelimiters delimiters) {
        this.delimiters = delimiters;
    }

    public List<String> parse(final String input) {
        String processedInput = delimiters.extractAndAddCustomDelimiter(input);
        return Arrays.asList(processedInput.split(getDelimiterRegex()));
    }

    private String getDelimiterRegex() {
        return delimiters.getRegex();
    }
}