package calculator;

import java.util.List;

public class StringCalculator {

    public int add(String input) {
        if (isBlank(input)) {
            return 0;
        }
        Delimiter delimiter = new Delimiter(input);
        List<Number> numbers = delimiter.extractNumbers(input);
        return numbers.stream()
            .peek(this::throwIfNegative)
            .reduce(Number::add)
            .orElse(Number.zero())
            .getValue();
    }

    private boolean isBlank(String input) {
        return input == null || input.isEmpty();
    }

    private void throwIfNegative(Number number) {
        if (number.isNegative()) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }

}
