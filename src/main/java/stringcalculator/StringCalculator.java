package stringcalculator;

import static stringcalculator.CalculateType.NON_NEGATIVE_INTEGER_ADDER;

import stringcalculator.parser.StringCalculatorParser;

public class StringCalculator {
    private final StringCalculatorParser stringCalculatorParser;

    public StringCalculator(StringCalculatorParser stringCalculatorParser) {
        this.stringCalculatorParser = stringCalculatorParser;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        ParsedNumbers parsedNumbers = stringCalculatorParser.execute(text);

        return NON_NEGATIVE_INTEGER_ADDER.getResult(parsedNumbers);
    }
}
