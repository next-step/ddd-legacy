package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        return add(text, new StringSplitter());
    }

    public int add(String text, StringSplitter splitter) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return Arrays.stream(splitter.split(text))
                .map(PositiveNumber::new)
                .reduce(PositiveNumber.ZERO, PositiveNumber::sum).intValue();
    }
}
