package kitchenpos.common.utils;

import static java.util.Arrays.stream;

public class NumberUtils {

    public static int sum(int[] numbers) {
        return stream(numbers).sum();
    }

    public static void validNativeNumberArray(String[] numbers) {
        stream(numbers).filter(number -> Integer.parseInt(number) < 0).forEach(number -> {
            throw new IllegalArgumentException();
        });
    }
    public static int[] fromStringArrayConvertToPositiveNumberArray(String[] stringArray) {
        validNativeNumberArray(stringArray);
        return stream(stringArray).mapToInt(Integer::parseInt).toArray();
    }

}
