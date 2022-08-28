package calculator;

import static java.lang.Integer.parseInt;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        return parseInt(text);
    }
}
