package calculator.shared;

import java.util.List;

public class NumberConvertor {
    public PositiveNumbers convertToPositiveNumbers(final List<String> values) {
        List<Integer> rawNumberList = values.stream()
                .map(Integer::parseInt)
                .toList();

        return PositiveNumbers.from(rawNumberList);
    }
}