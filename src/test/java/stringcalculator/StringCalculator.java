package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private final StringSplitter splitter;

    public StringCalculator() {
        this(new StringSplitter());
    }

    public StringCalculator(StringSplitter splitter) {
        this.splitter = splitter;
    }

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return Arrays.stream(splitter.split(text))
                .map(PositiveNumber::new)
                .reduce(PositiveNumber.ZERO, PositiveNumber::plus).value();
    }
}
