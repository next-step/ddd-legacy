package addstring;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private final List<Number> numbers;

    public Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(Number::getIntValue)
            .sum();
    }

    static public Numbers from(String[] stringNumbers) {
        List<Number> numbers = Arrays.stream(stringNumbers)
            .map(Number::new)
            .collect(Collectors.toList());
        return new Numbers(numbers);
    }

}
