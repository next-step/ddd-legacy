package calculator.application;

import calculator.domain.PositiveNumber;
import calculator.domain.PositiveNumbers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final int RETURN_VALUE_WHEN_EMPTY_OR_NULL = 0;

    public int add(String inputText) {
        if (validateInputText(inputText)) {
            return RETURN_VALUE_WHEN_EMPTY_OR_NULL;
        }

        return PositiveNumbers.from(createPositiveNumbers(inputText))
            .sum();
    }

    private boolean validateInputText(String inputText) {
        return inputText == null || inputText.trim().isEmpty();
    }

    private List<PositiveNumber> createPositiveNumbers(String inputText) {
        return Arrays.stream(stringToInt(inputText))
            .mapToObj(PositiveNumber::from)
            .collect(Collectors.toList());
    }

    private int[] stringToInt(String inputText) {
        return Arrays.stream(DelimiterParser.splitText(inputText))
            .mapToInt(Integer::parseInt)
            .toArray();
    }
}
