package calculator;

import calculator.calculate.CalculateStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    public static final int ZERO = 0;

    private List<Integer> numbers;

    public Numbers(String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .peek(number -> validateNegative(number))
                .boxed()
                .collect(Collectors.toList());
    }

    public int calculate(CalculateStrategy strategy) {
        return this.numbers.stream()
                .reduce(strategy::calculate)
                .orElse(ZERO);
    }

    public void validateNegative(int number) {
        if (number < ZERO) {
            throw new RuntimeException("음수는 사용할 수 없습니다");
        }
    }
}
