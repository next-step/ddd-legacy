package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {
    public int add(String text) {
        if (isBlank(text)) {
            return 0;
        }
        String[] numbers = split(text);
        return Stream.of(numbers).mapToInt(Integer::parseInt).sum();
    }

    private String[] split(String text) {
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return text.split("[,:]");
    }

    private boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}
