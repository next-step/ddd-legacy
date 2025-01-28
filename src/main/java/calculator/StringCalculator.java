package calculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        int result = 0;

        String[] numberString = text.split(",|:");
        result = Arrays.stream(numberString).mapToInt(Integer::parseInt).sum();

        return result;
    }
}
