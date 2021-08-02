package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SumCalculateStrategy implements CalculateStrategy {
    @Override
    public int calculate(final String[] text) {
        int sum = 0;
        List<Number> numbers = Stream.of(text)
                                    .map(Number::new)
                                    .collect(Collectors.toList());

        for (Number number : numbers) {
            sum += number.getValue();
        }

        return sum;
    }
}
