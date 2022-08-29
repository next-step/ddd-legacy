package calculate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private final List<PositiveNumber> numbers;

    public Numbers(String[] numbers) {
        this(toList(numbers));
    }

    public Numbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    private static List<PositiveNumber> toList(String[] numbers) {
        return Arrays.stream(numbers)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }

}
