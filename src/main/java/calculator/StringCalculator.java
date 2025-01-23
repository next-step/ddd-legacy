package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final NumberExtractor numberExtractor;
    private final NumberValidator numberValidator;


    public StringCalculator(NumberExtractor numberExtractor, NumberValidator numberValidator) {
        this.numberExtractor = numberExtractor;
        this.numberValidator = numberValidator;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        InputText inputText = new InputText(text);

        String[] numbers = numberExtractor.extractNumber(inputText.getNumberText(), inputText.getDelimiters());

        numberValidator.validateNumbers(numbers);

        return sum(numbers);
    }

    private int sum(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }

}
