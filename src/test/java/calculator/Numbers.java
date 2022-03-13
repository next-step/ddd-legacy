package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Numbers {

    private List<PositiveNumber> numbers;

    public Numbers(String[] numbers) {
        this.numbers = Arrays.stream(numbers)
                .map(number -> new PositiveNumber(number))
                .collect(Collectors.toList());
    }

    public int sum() {
        PositiveNumber positiveNumber = numbers.stream()
                .reduce((firstPositiveNumber, secondPositiveNumber) -> firstPositiveNumber.add(secondPositiveNumber))
                .get();
        return positiveNumber.getNumber();
    }
}
