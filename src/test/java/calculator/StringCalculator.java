package calculator;

import java.util.stream.Stream;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        String[] numbers = text.split(",");
        return Stream.of(numbers).mapToInt(Integer::parseInt).sum();
    }
}
