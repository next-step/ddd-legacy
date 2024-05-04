package stringcalculator;

import java.util.List;

public class StringCalculator {
    private static final String DELIMITERS = "[,:]";
    private final StringCalculatorParser stringCalculatorParser;

    public StringCalculator(StringCalculatorParser stringCalculatorParser) {
        this.stringCalculatorParser = stringCalculatorParser;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        List<Integer> result = stringCalculatorParser.execute(text);

        if (result.stream().anyMatch(it -> it < 0)) {
            throw new RuntimeException("음수는 처리하지 않습니다");
        }
        return result.stream().reduce(0, Integer::sum);
    }
}
