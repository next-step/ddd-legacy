package calculate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<PositiveNumber> numbers;

    public Numbers(String[] numbers) {
        this.numbers =  Arrays.stream(numbers)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public int calculate() {
        return numbers.stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }

}
