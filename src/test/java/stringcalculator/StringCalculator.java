package stringcalculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[,]");


    public int add(String source) {
        if (isNullOrEmpty(source)) {
            return 0;
        }
        return Arrays.stream(DEFAULT_DELIMITER_PATTERN.split(source))
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);
    }

    private boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }
}
