package calculator;

import java.util.stream.Stream;

public class NumberMapper {

    public Stream<Number> toNumbers(String[] tokens) {
        return Stream.of(tokens).map(Number::of);
    }
}