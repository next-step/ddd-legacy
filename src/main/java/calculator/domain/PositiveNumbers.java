package calculator.domain;

import java.util.Collections;
import java.util.List;

public class PositiveNumbers {

    private final List<PositiveNumber> operands;

    public PositiveNumbers(final List<PositiveNumber> operands) {
        this.operands = Collections.unmodifiableList(operands);
    }

    public int sum() {
        return operands.stream()
                .reduce(PositiveNumber::sum)
                .get()
                .getNumber();
    }
}
