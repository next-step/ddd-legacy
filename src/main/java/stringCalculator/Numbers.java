package stringCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Numbers {
    private final List<Number> numbers;

    private Numbers(List<Number> numbers) {
        this.numbers = Collections.unmodifiableList(numbers);
    }

    public static Numbers from(List<String> numbers) {
       return  new Numbers(numbers.stream().map(Number::from).toList());
    }

    public static Numbers empty() {
        return new Numbers(new ArrayList<>());
    }

    public boolean isNegative() {
        return numbers.stream().anyMatch(Number::isNegative);
    }

    public boolean isEmpty() {
        return numbers.isEmpty();
    }

    public Integer sum() {
        return numbers.stream().mapToInt(Number::getNumber).sum();
    }
}

