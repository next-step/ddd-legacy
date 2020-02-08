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
                                             .mapToInt(token -> PositiveNumber.from(token).val)
                                             .sum();
    }

    public static class PositiveNumber {
        public final int val;

        public static PositiveNumber from(String positiveNumber) {
            return new PositiveNumber(Integer.parseInt(positiveNumber));
        }

        private PositiveNumber(int val) {
            if (val < 0) { throw new RuntimeException(); }
            this.val = val;
        }
    }
}
