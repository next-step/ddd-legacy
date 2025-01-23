package calculator;

import java.util.Arrays;

public record SumCalculator() {

    public int calculate(int[] numbers) {
        return Arrays.stream(numbers).sum();
    }
}
