package stringCalculator;

import java.util.Arrays;

public class SumProcess {
    private final Input input;

    public SumProcess(String text) {
        input = new Input(text);
    }

    public int sum(){
        return Arrays.stream(input.getInputStrings())
                .filter(a -> this.isPositiveNumber(a))
                .mapToInt(a -> Integer.parseInt(a))
                .sum();
    }

    private static boolean isPositiveNumber(String input) {
        try {
            int num = Integer.parseInt(input);

            if (num < 0) {
                throw new RuntimeException("input included negative number");
            }

            return true;
        } catch (Exception e) {
            throw e;
        }
    }
}
