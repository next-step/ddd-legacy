package calculator.vo;

import java.util.List;

public record Numbers(List<PositiveNumber> numbers) {

    public static Numbers fromTokens(List<String> tokens) {
        return new Numbers(tokens.stream()
            .map(token -> new PositiveNumber(Integer.parseInt(token)))
            .toList());
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(PositiveNumber::value)
            .sum();
    }

    @Override
    public String toString() {
        return numbers.toString();
    }
}
