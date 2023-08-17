package calculator;

import java.util.Arrays;

public class Calculator {

    private final NumberParseUtils numberParseUtils;
    private final TokenizerUtils tokenizerUtils;

    public Calculator(NumberParseUtils numberParseUtils, TokenizerUtils tokenizerUtils) {
        this.numberParseUtils = numberParseUtils;
        this.tokenizerUtils = tokenizerUtils;
    }


    public int calc(String input) {
        if (isEmpty(input)) {
            return 0;
        }

        String[] strings = tokenizerUtils.parse(input);
        int[] numbers = numberParseUtils.parse(strings);

        return Arrays.stream(numbers)
            .sum();
    }

    private boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
}
