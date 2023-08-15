package stringaddcalculator;

import java.util.Arrays;

public class Separator {
    private static final String DELIMITER = ",|:";

    public int[] separate(String expression) {
        return Arrays.stream(expression.split(DELIMITER))
                .mapToInt(Integer::parseInt)
                .toArray();
    }
}
