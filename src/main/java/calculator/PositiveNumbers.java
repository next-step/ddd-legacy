package calculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers {
    private final List<Integer> numbers;

    public PositiveNumbers(String[] values) {
        List<Integer> intValues = changeValueToInt(values);
        validateNegativeValue(intValues);
        this.numbers = Collections.unmodifiableList(intValues);
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    private void validateNegativeValue(List<Integer> values) {
        boolean hasNegative = values.stream().anyMatch(this::isNegative);

        if(hasNegative) {
            throw new RuntimeException("음수는 올 수 없습니다.");
        }
    }

    private List<Integer> changeValueToInt(String[] values) {
        return Arrays.stream(values).map(Integer::valueOf).collect(Collectors.toList());
    }

    private boolean isNegative(int value) {
        if (value < 0) {
            return true;
        }
        return false;
    }

}
