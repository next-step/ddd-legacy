package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {

    public static final int DEFAULT_VALUE = 0;

    public int add(final String text) {
        if (this.isEmpty(text)) {
            return DEFAULT_VALUE;
        }

        StringDelimiter delimiter = new StringDelimiter();
        List<Number> numbers = Stream.of(delimiter.parse(text))
                                    .map(Number::new)
                                    .collect(Collectors.toList());

        return this.calculate(numbers);
    }

    private boolean isEmpty(final String text) {
        return text == null || text.isEmpty();
    }

    private int calculate(final List<Number> numbers) {
        int sum = 0;
        for (Number number : numbers) {
            sum += number.getValue();
        }
        return sum;
    }
}
