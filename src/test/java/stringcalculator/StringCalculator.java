package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private String inputString;

    public StringCalculator() {
    }

    //
    public int sum(String inputString) {

        String[] split = inputString.split("");

        int sum = Arrays.stream(split)
                .filter(s -> !s.contains(":"))
                .filter(
                        s -> !s.contains(",")
                ).mapToInt(Integer::parseInt)
                .sum();

        return sum;

    }
}
