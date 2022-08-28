package calculator;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class StringCalculator {

    private static final String DELIMITER_REGEX = ",|:";

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        String[] tokens = text.split(DELIMITER_REGEX);

        return Arrays.stream(tokens)
                .mapToInt(this::parseIntegerFromToken)
                .sum();
    }

    private int parseIntegerFromToken(String token) {
        return parseInt(token);
    }
}
