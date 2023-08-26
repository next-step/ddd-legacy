package calculator.application;

import calculator.domain.Digit;

import java.util.Arrays;

public class StringCalculator {

    private static final int RETURN_VALUE_WHEN_EMPTY_OR_NULL = 0;

    public int add(String inputText) {
        if (validateInputText(inputText)) {
            return RETURN_VALUE_WHEN_EMPTY_OR_NULL;
        }

        return Arrays.stream(stringToInt(inputText))
            .mapToObj(Digit::from)
            .reduce(Digit.from(0), Digit::add)
            .getValue();
    }

    private boolean validateInputText(String inputText) {
        return inputText == null || inputText.trim().isEmpty();
    }

    private int[] stringToInt(String inputText) {
        return Arrays.stream(DelimiterParser.splitText(inputText))
            .mapToInt(Integer::parseInt)
            .toArray();
    }
}
