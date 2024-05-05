package calculator2;

import java.util.stream.Stream;

public class Calculator {

    public int sum(Stream<Number> numbers) {
        return numbers.mapToInt(Number::getNumber).sum();
    }
}