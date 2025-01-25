
package stringcalculator;

import java.util.List;

public class ZeroOrPositiveNumbers {

    private final List<ZeroOrPositiveNumber> numbers;

    public ZeroOrPositiveNumbers(List<ZeroOrPositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public ZeroOrPositiveNumber sum() {
        ZeroOrPositiveNumber totalSum = new ZeroOrPositiveNumber(0);
        for (ZeroOrPositiveNumber number : numbers) {
            totalSum = totalSum.sum(number);
        }
        return totalSum;
    }
}

