package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final CustomDelimiterExtractor customDelimiterExtractor;
    private final TextSplitter textSplitter;
    private final NumberValidator numberValidator;


    public StringCalculator(CustomDelimiterExtractor customDelimiterExtractor, TextSplitter textSplitter, NumberValidator numberValidator) {
        this.customDelimiterExtractor = customDelimiterExtractor;
        this.textSplitter = textSplitter;
        this.numberValidator = numberValidator;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        InputText inputText = customDelimiterExtractor.extractDelimiter(text);

        String[] numbers = textSplitter.splitText(inputText);

        numberValidator.validateNumbers(numbers);

        return sum(numbers);
    }

    private int sum(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }

}
