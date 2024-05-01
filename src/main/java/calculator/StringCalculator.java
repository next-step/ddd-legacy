package calculator;

import calculator.util.SplitterUtils;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return Number.ZERO_NUMBER;
        }

        Numbers numbers = new Numbers(SplitterUtils.split(text));
        return numbers.getSum();
    }
}
