package stringcalculator;

import java.util.List;

public class StringCalculator {

    public int add(final String text) {
        List<Number> parsedNumbersFromText = NumberParser.parse(text);
        return parsedNumbersFromText.stream().mapToInt(Number::getValue).sum();
    }
}
