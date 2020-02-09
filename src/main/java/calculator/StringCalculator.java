package calculator;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int ZERO = 0;
    private static final Pattern CUSTOM_DELIMITER_REGEX = Pattern.compile("//(.)\n(.*)");

    int add(final String text) {
        if (StringUtils.isBlank(text)) {
            return ZERO;
        }

        String[] stringNumbers = text.split("[,:]");

        Matcher m = CUSTOM_DELIMITER_REGEX.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            stringNumbers = m.group(2).split(customDelimiter);
        }

        return Arrays.stream(stringNumbers).map(Integer::parseInt).filter(a -> {
            if (a < 0) {
                throw new RuntimeException();
            }
            return true;
        }).reduce(0, Integer::sum);
    }

}
