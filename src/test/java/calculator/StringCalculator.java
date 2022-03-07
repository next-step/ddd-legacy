package calculator;

import java.util.Arrays;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class StringCalculator {

    public static int calculate(String value) {
        String[] numberValues = Syntax.parse(value);
        return Arrays.stream(numberValues)
                .map(PositiveNumber::new)
                .collect(collectingAndThen(toList(), PositiveNumbers::new))
                .sum();
    }
}
