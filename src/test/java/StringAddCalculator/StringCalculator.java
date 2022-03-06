package StringAddCalculator;

import java.util.Arrays;

public class StringCalculator {

    public static final int ZERO_NUMBER = 0;

    public int add(String text) {
        if(isNullOrBlank(text)) {
            return ZERO_NUMBER;
        }

        return Arrays.stream(PatternMatcherUtils.customDelimit(text))
                .mapToInt(Integer::valueOf)
                .filter(this::isNegativeValue)
                .sum();
    }

    private boolean isNullOrBlank(String text) {
        return (text == null || text.isEmpty());
    }


    private boolean isNegativeValue(int text) {
        if(text < ZERO_NUMBER) {
            throw new RuntimeException("음수는 처리할 수 없습니다.");
        }
        return true;
    }


}
