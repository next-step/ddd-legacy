package calculator;

import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return Number.ZERO_NUMBER;
        }

        Numbers numbers = makeNumbers(SplitterUtils.split(text));
        return numbers.getSum();
    }

    private Numbers makeNumbers(String[] tokens) {
        List<Number> numbers = Arrays.stream(tokens)
                .map(Number::new)
                .toList();

        return new Numbers(numbers);
    }
}
