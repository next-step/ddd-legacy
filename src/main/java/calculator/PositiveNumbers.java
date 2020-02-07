package calculator;

import java.util.ArrayList;
import java.util.List;

public class PositiveNumbers {
    private final List<Integer> positiveNumbers;

    public PositiveNumbers(List<Integer> numbers) {
        validate(numbers);
        this.positiveNumbers = new ArrayList<Integer>(numbers); // 파라미터 리스트를 복사하여 immutable 하도록..
    }

    private void validate(List<Integer> numbers) {
        numbers.forEach(number -> {
            if (number < 0) {
                throw new RuntimeException("음수는 사용할 수 없습니다. (" + number + ")");
            }
        });
    }

    public int sum() {
        return positiveNumbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
