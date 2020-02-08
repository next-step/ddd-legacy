package calculator;

import org.apache.logging.log4j.util.Strings;

public class StringCalculator {

    public int add(final String text) {
        if (Strings.isBlank(text)) {
            return 0;
        }
        if (text.length() == 1) {
            return Integer.parseInt(text);
        }
        return 0;
    }
}
