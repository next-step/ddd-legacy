package StringAddCalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        return Arrays.stream(PatternMatcherUtils.customDelimit(text))
                .mapToInt(Integer::valueOf)
                .filter(this::isNegativeValue)
                .sum();
    }


    private boolean isNegativeValue(int text) {
        if(text < 0) {
            throw new RuntimeException("음수는 처리할 수 없습니다.");
        }
        return true;
    }


}
