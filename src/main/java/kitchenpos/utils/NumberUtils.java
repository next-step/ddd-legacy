package kitchenpos.utils;

import static java.util.Arrays.stream;

public class NumberUtils {

    public static int sum(String[] numbers) {
        return stream(numbers).mapToInt(Integer::parseInt).sum();
    }

    public static void validNativeNumber(String[] numbers) {
        for (String number : numbers) {
            if (Integer.parseInt(number) < 0) {
                throw new IllegalArgumentException();
            }
        }
    }

}
