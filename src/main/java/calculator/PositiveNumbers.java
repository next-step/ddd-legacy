package calculator;

import java.util.List;

public class PositiveNumbers {
    private final List<PositiveNumber> positiveNumbers;

    public PositiveNumbers(List<PositiveNumber> positiveNumbers) {
        this.positiveNumbers = positiveNumbers;
    }

    public PositiveNumber addTotalPositiveNumbers() {
        return positiveNumbers.stream()
                .reduce(PositiveNumber.ZERO ,PositiveNumber::sum);
    }
}
