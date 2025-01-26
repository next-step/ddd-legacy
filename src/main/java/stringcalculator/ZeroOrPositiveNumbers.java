
package stringcalculator;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ZeroOrPositiveNumbers {

    private final List<ZeroOrPositiveNumber> numbers;

    public ZeroOrPositiveNumbers(String[] numbers) {
        this(toNumbers(numbers));
    }

    private static List<ZeroOrPositiveNumber> toNumbers(String[] numbers) {
        return Arrays.stream(numbers)
                .map(ZeroOrPositiveNumber::new)
                .collect(toList());
    }

    public ZeroOrPositiveNumbers(List<ZeroOrPositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        ZeroOrPositiveNumber totalSum = new ZeroOrPositiveNumber(0);
        for (ZeroOrPositiveNumber number : numbers) {
            totalSum = totalSum.sum(number);
        }
        return totalSum.getNumber();
    }
}

