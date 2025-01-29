package calculator;

import java.util.Arrays;

public class StringCalculator  {

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        if (input.length() == 1) {
            return Integer.parseInt(input);
        }

        String[] split = input.split("[,:]");
        return Arrays.stream(split)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
