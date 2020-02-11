package calculator;


import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;

public class StringCalculator {

    public int sumOfExtractedNumber(String text) {
        if (Strings.isBlank(text)) {
            return PositiveNumber.ZERO.getValue();
        }

        String[] tokens = StringTokenizer.tokenize(text);
        return Arrays.stream(tokens)
                .map(PositiveNumber::valueOf)
                .reduce(PositiveNumber.ZERO, PositiveNumber::sum)
                .getValue();
    }
}
