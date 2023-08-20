package stringCalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        if (text.length() == 1) {
            return Integer.parseInt(text);
        }

        return Arrays.stream(extractNumbersUsingDelimiter(text))
            .mapToInt(this::parsePositiveInt)
            .sum();
    }

    private String[] extractNumbersUsingDelimiter(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (hasCustomDelimiter(m)) {
            final String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split(",|:");
    }

    private boolean hasCustomDelimiter(Matcher m) {
        return m.find();
    }

    private int parsePositiveInt(String text) {
        int textToInt = Integer.parseInt(text);
        if (textToInt < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
        return textToInt;
    }

}
