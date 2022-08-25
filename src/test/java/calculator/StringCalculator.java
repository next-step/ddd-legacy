package calculator;

import calculator.utils.StringUtils;
import java.util.List;

public final class StringCalculator {

    private final StringNumberParser stringNumberParser;

    public StringCalculator() {
        stringNumberParser = new StringNumberParser();
    }

    public int add(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }

        return toStringNumbers(text)
                .stream()
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private List<String> toStringNumbers(String text) {
        return stringNumberParser.convertToList(text);
    }
}
