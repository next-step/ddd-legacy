package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringAdditionCalculator {
    public Integer calculate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        String separators = ",:";
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            separators += m.group(1);
            text = m.group(2);
        }
        return Arrays.stream(text.split("[" + separators + "]"))
            .mapToInt(this::parseInt)
            .peek(this::checkNegative)
            .sum();
    }

    private Integer parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new RuntimeException();
        }
    }

    private void checkNegative(Integer number) {
        if (number < 0) {
            throw new RuntimeException();
        }
    }
}
