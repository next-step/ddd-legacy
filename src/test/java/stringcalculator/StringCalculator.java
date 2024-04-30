package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    public static int getSum(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }
        String[] split = input.trim().split("[,:]");
        int result = 0;
        for (String s : split) {
            result += Integer.valueOf(s);
        }
        return result;
    }
}
