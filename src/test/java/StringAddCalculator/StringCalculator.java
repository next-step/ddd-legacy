package StringAddCalculator;

import java.util.Arrays;

public class StringCalculator {

    private static final int ZERO_NUMBER = 0;

    public int add(String text) {
        if(isNullOrBlank(text)) {
            return ZERO_NUMBER;
        }

        return Arrays.stream(new PatternMatcher(text).customDelimit(text))
                .map(PositiveNumber::new)
                .mapToInt(PositiveNumber::intValue)
                .sum();
    }

    private boolean isNullOrBlank(String text) {
        return (text == null || text.isEmpty());
    }


}
