package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final DelimiterGroup delimiterGroup;
    private final NumberExtractor numberExtractor;


    public StringCalculator(DelimiterGroup delimiterGroup, NumberExtractor numberExtractor) {
        this.delimiterGroup = delimiterGroup;
        this.numberExtractor = numberExtractor;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        String numberText = delimiterGroup.extractCustomDelimiter(text);

        Integer[] numbers = numberExtractor.extract(numberText, delimiterGroup);

        return sum(numbers);
    }

    private int sum(Integer[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::intValue)
                .sum();
    }

}
