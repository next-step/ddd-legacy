package calculator;

import java.util.Arrays;

public class StringNumbers {

    private final PositiveNumber[] numbers;


    public StringNumbers(String[] numbers) {
        this.numbers = Arrays.stream(numbers)
                .map(PositiveNumber::new)
                .toArray(PositiveNumber[]::new);
    }

    public int sum() {
        return numbers.length == 0 ? 0 : Arrays.stream(numbers)
                .mapToInt(PositiveNumber::getValue)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringNumbers other = (StringNumbers) o;
        return Arrays.equals(numbers, other.numbers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(numbers);
    }

}
