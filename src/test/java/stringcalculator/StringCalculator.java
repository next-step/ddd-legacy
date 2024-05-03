package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.platform.commons.util.StringUtils.isBlank;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_SYMBOL = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

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
                throw new RuntimeException("음수는 입력할 수 없습니다.");
            }
        }
    }

    public String[] splitByDelimiter(String text) {
        Matcher matched = CUSTOM_DELIMITER_PATTERN.matcher(text);

        if (matched.find()) {
            String customDelimiter = matched.group(1);
            String textSource = matched.group(2);

            return textSource
                    .split(customDelimiter);

        }

        return text.split(DEFAULT_DELIMITER_SYMBOL);
    }

}
