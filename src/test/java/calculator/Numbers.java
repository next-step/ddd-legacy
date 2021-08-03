package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Numbers {
    private final List<Integer> numbers;

    public Numbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public static Numbers convertToNumbers(String text) {
        return new Numbers(Stream.of(StringUtil.split(text))
                .map(Integer::parseInt)
                .peek(Numbers::isNegative)
                .collect(Collectors.toList()));
    }

    private static void isNegative(int n) {
        if (n < 0) {
            throw new RuntimeException();
        }
    }

    public int sum() {
        return this.numbers.stream()
                .mapToInt(i -> i)
                .sum();
    }
}
