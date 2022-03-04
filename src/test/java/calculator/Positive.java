package calculator;

import java.util.Arrays;

public class Positive {
    public static int[] parseInt(final String[] numbers) {
        int[] result = null;
        try {
            result = Arrays.stream(numbers)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int sum(final int[] numbers) {
        return Arrays.stream(numbers).sum();
    }

    public static boolean doesHaveNegative(final int[] numbers) {
        return Arrays.stream(numbers).anyMatch(Positive::isNegative);
    }

    public static boolean isNegative(int number) {
        return number < 0;
    }
}
