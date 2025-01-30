package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private List<Integer> numbers = new ArrayList<>();

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
        return numbers.stream().anyMatch(n -> n < 0);
    }

    public int sum() {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }

    /* convertNumber : String > Integer 변환 */
    public static int convertNumber(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException("non-numeric found : " + input);
        }
    }

    public List<Integer> getNumbers() {
        return numbers;
    }
}
