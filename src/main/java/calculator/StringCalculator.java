package calculator;

import calculator.splitter.Splitter;
import calculator.splitter.StringSplitter;
import org.springframework.util.StringUtils;

public class StringCalculator {

    private final Splitter<String> splitter;

    public StringCalculator() {
        this.splitter = new StringSplitter();
    }

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return PositiveNumber.ZERO_NUMBER;
        }

        PositiveNumbers numbers = new PositiveNumbers(splitter.split(text));
        return numbers.sum();
    }
}
