package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;

public class StringCalculator {

    public int add(final String text) {
        if (Strings.isBlank(text)) {
            return 0;
        }
        if (text.contains(",") || text.contains(":")) {
            return Arrays.stream(text.split("[,:]+"))
                    .mapToInt(Integer::parseInt)
                    .sum();
        }
        return Integer.parseInt(text);
    }
}
