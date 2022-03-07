package stringcalculator;

import java.util.List;

public class StringCalculator {

    public static int add(List<PositiveNumber> numbers) {
        PositiveNumber result = numbers.stream()
                .reduce(PositiveNumber.zero(), PositiveNumber::plus);

        return result.getValue();
    }
}
