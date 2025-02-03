package calculator.vo;

import java.util.List;

public record PositiveNumbers(List<PositiveNumber> numbers) {

    public static PositiveNumbers fromTokens(List<String> tokens) {
        return new PositiveNumbers(tokens.stream()
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PositiveNumbers that)) return false;
        return numbers.equals(that.numbers);
    }

    @Override
    public int hashCode() {
        return numbers.hashCode();
    }
}
