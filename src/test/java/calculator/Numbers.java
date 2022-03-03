package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private List<PositiveNumber> numbers;

    public Numbers(String[] numbers) {
        this.numbers = Arrays.stream(numbers)
                .map(number -> new PositiveNumber(number))
                .collect(Collectors.toList());
    }

    public int sum() {
        PositiveNumber number = new PositiveNumber();

        numbers.forEach(positiveNumber -> number.add(positiveNumber));

        return number.getNumber();
    }
}
