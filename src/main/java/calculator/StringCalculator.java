package calculator;

import java.util.Arrays;

public class StringCalculator  {

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        if (input.startsWith("//")) {
            String[] split = input.split("\n");
            String delimiter = split[0].substring(2);
            String numbers = split[1];
            return Arrays.stream(numbers.split(delimiter))
                    .map(PositiveNumber::new)
                    .reduce(PositiveNumber::add)
                    .orElseThrow()
                    .value();
        }

        String[] split = input.split("[,:]");
        return Arrays.stream(split)
                .map(PositiveNumber::new)
                .reduce(PositiveNumber::add)
                .orElseThrow()
                .value();
    }
}
