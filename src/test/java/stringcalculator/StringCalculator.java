package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private String inputString;

    public StringCalculator() {
    }

    public int sum(String inputString) {

        String[] split = inputString.split(",|:");
        return Arrays.stream(split).mapToInt(Integer::parseInt).sum();
    }
}
