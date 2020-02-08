package calculator;

import java.util.List;
import java.util.stream.Collectors;

class PositiveNumbers {
    private List<PositiveNumber> numbers;

    PositiveNumbers(List<Integer> values) {
        this.numbers = convertPositiveNumbers(values);
    }

    Integer sum(int defaultResult) {
        return numbers.stream()
                .reduce(PositiveNumber.from(defaultResult), PositiveNumber::sum)
                .getValue();
    }

    private List<PositiveNumber> convertPositiveNumbers(List<Integer> values) {
        return values.stream()
                .map(PositiveNumber::from)
                .collect(Collectors.toList());
    }

}
