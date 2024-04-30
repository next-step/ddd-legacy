package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class StringCalculator {
    public static int getSum(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        return Arrays.stream(input.trim().split("[,:]"))
                .mapToInt(Integer::valueOf)
                .sum();
    }
}
