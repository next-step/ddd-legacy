package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (StringUtils.isEmpty(text)) {
            return PositiveNumber.ZERO.getValue();
        }

        String[] tokens = StringTokenizer.tokenize(text);
        return Arrays.stream(tokens)
                .map(PositiveNumber::valueOf)
                .reduce(PositiveNumber.ZERO, PositiveNumber::sum)
                .getValue();
    }
}
