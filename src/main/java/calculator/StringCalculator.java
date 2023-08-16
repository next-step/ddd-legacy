package calculator;

import java.util.Arrays;

public class StringCalculator {
    public int run(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        if (str.length() == 1) {
            return Integer.parseInt(str);
        }

        String[] numbers = str.split("[,:]");
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
