package calculator;

import java.util.stream.Stream;

public class StringCalculator {
    public int add(String text) {
        if (isBlank(text)) {
            return 0;
        }
        return Stream.of(StringUtil.split(text))
                .mapToInt(Integer::parseInt)
                .peek(this::isNegative)
                .sum();
    }

    private void isNegative(int n) {
        if (n < 0) {
            throw new RuntimeException();
        }
    }


    private boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}
