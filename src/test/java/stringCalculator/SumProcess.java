package stringCalculator;

import java.util.Arrays;

public class SumProcess {

    public SumProcess() {
    }

    public int sum(String[] numbers){
        this.validationNumber(numbers);

        return Arrays.stream(numbers)
//                .filter(a -> this.isPositiveNumber(a))
                .mapToInt(a -> Integer.parseInt(a))
                .sum();
    }

    private void validationNumber(String[] numbers) {
        Arrays.stream(numbers).forEach(a -> {
            this.isPositiveNumber(a);
        });
    }

    private void isPositiveNumber(String input) {
        try {
            int num = Integer.parseInt(input);

            if (num < 0) {
                throw new RuntimeException("input included negative number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("input is not number");
        }
    }
}
