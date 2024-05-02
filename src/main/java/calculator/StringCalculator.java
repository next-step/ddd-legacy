package calculator;

import calculator.util.SplitterUtils;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return PositiveNumber.ZERO_NUMBER;
        }

        PositiveNumbers numbers = new PositiveNumbers(SplitterUtils.split(text));
        return numbers.sum();
    }
}
