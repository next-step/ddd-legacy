package calculator.vo;

import java.util.List;

public record Numbers(List<Integer> numbers) {

    public static Numbers fromTokens(List<String> tokens) {
        return new Numbers(tokens.stream()
            .map(Integer::parseInt)
            .toList());
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(Integer::intValue)
            .sum();
    }

    @Override
    public String toString() {
        return numbers.toString();
    }
}
