package calculator;

import java.util.Collections;
import java.util.List;

public class PositiveNumbers {
    private final List<PositiveNumber> positiveNumbers;

    public PositiveNumbers(final List<PositiveNumber> positiveNumbers) {
        this.positiveNumbers = Collections.unmodifiableList(positiveNumbers);
    }

    public int sum() {
        return positiveNumbers.stream()
            .mapToInt(PositiveNumber::getNumber)
            .sum();
    }
}
