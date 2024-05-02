package calculator;

import org.springframework.util.StringUtils;

public class StringCalculator {

    private final Splitter splitter;

    public StringCalculator(Splitter splitter) {
        this.splitter = splitter;
    }

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return PositiveNumber.ZERO_NUMBER;
        }

        PositiveNumbers numbers = new PositiveNumbers(splitter.split(text));
        return numbers.sum();
    }
}
