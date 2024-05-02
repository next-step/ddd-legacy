package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return Arrays.stream(new StringSplitter().split(text))
                .map(PositiveNumber::new)
                .reduce(PositiveNumber.ZERO, PositiveNumber::sum).intValue();
    }
}
