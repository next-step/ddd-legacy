package calculator;

import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;

public class StringCalculator {

    static final String COMMA_DELIMITER = ",";

    public int add(final String text) {
        if (Strings.isBlank(text)) {
            return 0;
        }
        if (text.length() == 1) {
            return Integer.parseInt(text);
        }
        return Arrays.stream(text.split(COMMA_DELIMITER))
                     .mapToInt(Integer::parseInt)
                     .sum();
    }
}
