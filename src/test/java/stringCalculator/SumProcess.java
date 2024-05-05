package stringCalculator;

import java.util.Arrays;

public class SumProcess {

    public SumProcess() {
    }

    public int sum(String[] numbers){
        this.validateNumber(numbers);

        return Arrays.stream(numbers)
//                .filter(a -> this.isPositiveNumber(a))
                .mapToInt(a -> Integer.parseInt(a))
                .sum();
    }

    private void validateNumber(String[] numbers) {
        Arrays.stream(numbers).forEach(a -> {
            this.validateNumberFormatAndPositiveNumber(a);
        });
    }

    private void validateNumberFormatAndPositiveNumber(String input) {
        try {
            int num = Integer.parseInt(input);

            if (num < 0) {
                throw new RuntimeException("input included negative number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("input is invalid value. please input string type number (input : "+input+")");
        }
    }
}
