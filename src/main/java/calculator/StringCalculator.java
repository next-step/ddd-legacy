package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {
    private final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public StringCalculator() {
    }

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return Stream.of(parseNumbers(text))
                .map(Integer::parseInt)
                .map(PositiveNumber::new)
                .reduce(PositiveNumber.ZERO, PositiveNumber::plus)
                .value();
    }

    private String[] parseNumbers(String text) {
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split("[,:]");
    }
}
