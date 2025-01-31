package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private List<Number> numbers = new ArrayList<>();

    public Numbers() {
    }

    public Numbers(List<String> numbers) {
        this.numbers = numbers.stream()
                .map(Numbers::convertNumber)
                .collect(Collectors.toList());
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
    public static int convertNumber(String input) {
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
