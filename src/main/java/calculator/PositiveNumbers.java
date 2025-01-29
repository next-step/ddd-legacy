package calculator;

import calculator.PositiveNumber;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 0 이상의 숫자들의 리스트를 관리하는 일급 컬렉션
 */
public class PositiveNumbers {

    private final List<PositiveNumber> numbers;

    public PositiveNumbers(final List<String> numberToStrings) {
        this.numbers = numberToStrings.stream()
            .map(PositiveNumber::new)
            .toList();
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(PositiveNumber::getValue)
            .sum();
    }
}
