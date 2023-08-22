package calculator;

import java.util.Arrays;

public class StringCalculator {

    private DelimiterSplit split = new DelimiterSplit();

    public int add(final String text) {
        if (isBlank(text)) {
            return 0;
        }

        String[] strValue = split.split(text);
        PositiveNumber number = Arrays.stream(strValue).map(PositiveNumber::toNumberValue)
                .reduce(PositiveNumber::addValue).orElseThrow(RuntimeException::new);

        return number.getValue();
    }

    private boolean isBlank(final String text) {
        return text == null || text.isEmpty();
    }

}
