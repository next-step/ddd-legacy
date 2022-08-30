package calculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        return tokenNumberSum(StringTokenUtils.tokenizer(text));
    }

    private int tokenNumberSum(String[] tokens) {
        return Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .reduce(PositiveNumber.zeroNumber(), PositiveNumber::add)
                .getNumber();
    }
}
