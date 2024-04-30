package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    static Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public static int getSum(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return splitAndGetSum(input, "[,:]");
        }

        String customSeparator = matcher.group(1);
        return splitAndGetSum(matcher.group(2), customSeparator);
    }

    private static int splitAndGetSum(String matcher, String separator) {
        try {
            return Arrays.stream(matcher.trim().split(separator))
                    .mapToInt(Integer::valueOf)
                    .peek(x -> {
                        if (x < 0) {
                            throw new IllegalArgumentException("음수값은 처리할 수 없습니다.");
                        }
                    })
                    .sum();
        } catch (NumberFormatException e) {
            throw new NumberFormatException("올바른 숫자 입력 값이 아닙니다.");
        }
    }
}