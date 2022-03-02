package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private List<PositiveNumber> numbers;

    private Numbers(String[] numbers) {
        this.numbers = Arrays.stream(numbers)
                .map(number -> PositiveNumber.create(Integer.parseInt(number)))
                .collect(Collectors.toList());
    }

    public static Numbers create(String[] numberTexts) {
        return new Numbers(numberTexts);
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }
}
