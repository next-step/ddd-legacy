package calculator;

import java.util.stream.Stream;

public class StringCalculator {
    public int add(String text) {
        if (isBlank(text)) {
            return 0;
        }
        String[] numbers = text.split(",");
        return Stream.of(numbers).mapToInt(Integer::parseInt).sum();
    }

    private boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}
