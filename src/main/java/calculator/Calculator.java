package calculator;

import java.util.Arrays;

public class Calculator {

    private final NumberParsePolicy numberParsePolicy;
    private final TokenizePolicy tokenizePolicy;

    public Calculator(NumberParsePolicy numberParsePolicy, TokenizePolicy tokenizePolicy) {
        this.numberParsePolicy = numberParsePolicy;
        this.tokenizePolicy = tokenizePolicy;
    }


    public int calc(String input) {
        if (isEmpty(input)) {
            return 0;
        }

        String[] strings = tokenizePolicy.parse(input);
        int[] numbers = numberParsePolicy.parse(strings);

        return Arrays.stream(numbers)
            .sum();
    }

    private boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
}
