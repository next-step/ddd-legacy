package calculator;

import java.util.List;

public class StringCalculator {

    public int add(String input) {
        if (isBlank(input)) {
            return 0;
        }
        List<PositiveNumber> positiveNumbers = Delimiter.extractNumbers(input);
        return positiveNumbers.stream()
            .reduce(PositiveNumber::add)
            .orElse(PositiveNumber.ZERO)
            .getValue();
    }

    private boolean isBlank(String input) {
        return input == null || input.isEmpty();
    }

}
