package calculator;

import java.util.ArrayList;
import java.util.List;

public class Numbers {

    private final List<Number> numbers;

    public Numbers() {
        this.numbers = new ArrayList<>();
    }

    public Numbers(List<String> stringNumbers) {
        this.numbers = stringNumbers.stream()
                .map(Numbers::convertNumber)
                .toList();
    }

    public boolean isNullOrEmpty() {
        return numbers == null || numbers.isEmpty();
    }

    public boolean hasNegativeNumber() {
        return numbers.stream().anyMatch(n -> n.intValue() < 0);
    }

    public int sum() {
        return numbers.stream().mapToInt(Number::intValue).sum();
    }

    /* convertNumber : String > Integer 변환 */
    public static Number convertNumber(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("non-numeric found : " + input);
        }
    }

    public List<Number> getNumbers() {
        return numbers;
    }
}
