package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.platform.commons.util.StringUtils.isBlank;

public class StringCalculator {

    private final String DEFAULT_DELIMITER_SYMBOL = "[,:]";

    public int add(String text) {

        if (isBlank(text)) {
            return 0;
        }
        return integerTransfer(text);
    }

    public Integer integerTransfer(String text) {
        String[] strings = splitByDelimiter(text);

        isNegative(strings);

        return Arrays.stream(strings)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    public void isNegative(String[] strings) {
        for (String value : strings) {
            if (Integer.parseInt(value) < 0) {
                throw new RuntimeException();
            }
        }
    }

    public String[] splitByDelimiter(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

        if (m.find()) {
            return m.group(2)
                    .split(m.group(1));

        }

        return text.split(DEFAULT_DELIMITER_SYMBOL);
    }

}
