package stringcalculator;

import java.util.Arrays;

public class Operations {

    public static int sum(int[] numbers) {
        return Arrays.stream(numbers).sum();
    }
}
