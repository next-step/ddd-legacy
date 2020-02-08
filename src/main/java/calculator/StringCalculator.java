package calculator;

import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;

public class StringCalculator {

    //    TODO : fix private after refactor test
    static final String COMMA_DELIMITER = ",";
    private static final String COLON_DELIMITER = ":";
    private static final String DEFAULT_DELIMITER = COMMA_DELIMITER + "|" + COLON_DELIMITER;

    public int add(final String text) {
        return Strings.isBlank(text) ? 0
                                     : Arrays.stream(text.split(DEFAULT_DELIMITER))
                                             .mapToInt(Integer::parseInt)
                                             .sum();
    }
}
