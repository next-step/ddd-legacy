package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final CustomDelimiterExtractor customDelimiterExtractor;
    private final NumberExtractor numberExtractor;
    private final NumberValidator numberValidator;


    public StringCalculator(CustomDelimiterExtractor customDelimiterExtractor, NumberExtractor numberExtractor, NumberValidator numberValidator) {
        this.customDelimiterExtractor = customDelimiterExtractor;
        this.numberExtractor = numberExtractor;
        this.numberValidator = numberValidator;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        InputText inputText = customDelimiterExtractor.extractDelimiter(text);

        String[] numbers = numberExtractor.extractNumber(inputText.numberText(), inputText.delimiters());

        numberValidator.validateNumbers(numbers);

        return sum(numbers);
    }

    private int sum(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }

}
