package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final NumberExtractor numberExtractor;


    public StringCalculator(NumberExtractor numberExtractor) {
        this.numberExtractor = numberExtractor;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        DelimiterGroup delimiterGroup = new DelimiterGroup(text);

        Integer[] numbers = numberExtractor.extract(delimiterGroup.getNumberText(), delimiterGroup.getDelimiterPattern());

        return sum(numbers);
    }

    private int sum(Integer[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::intValue)
                .sum();
    }

}
