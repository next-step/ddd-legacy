package calculator.domain;

import java.util.Arrays;
import java.util.List;

public class Numbers {
    private static final Number ZERO = new Number(0);

    private final List<Number> value;

    public Numbers(String[] numbers) {
        this.value = parseNumbers(numbers);
    }

    private List<Number> parseNumbers(String[] numbers) {
        return Arrays.stream(numbers)
                .map(this::parseNumber)
                .map(Number::new)
                .toList();
    }

    private int parseNumber(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 값이 포함되어 있습니다.");
        }
    }

    public int sum() {
        return value.stream()
                .reduce(ZERO, Number::add)
                .value();
    }
}
