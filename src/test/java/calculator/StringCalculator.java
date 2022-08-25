package calculator;

import io.micrometer.core.instrument.util.StringUtils;

public class StringCalculator {

    private final NumbersFactory numbersFactory;

    public StringCalculator() {
        this.numbersFactory = new NumbersFactory();
    }

    public int add(String input) {
        int sum = 0;
        if (StringUtils.isEmpty(input)) {
            return sum;
        }
        int[] numbers = numbersFactory.getNumbers(input);
        sum = getSum(sum, numbers);
        return sum;
    }

    private int getSum(int sum, int[] numbers) {
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

}
