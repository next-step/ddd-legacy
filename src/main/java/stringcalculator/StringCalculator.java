package stringcalculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int MIN_TEXT_LENGTH = 1;
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";

    public int add(final String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }
        if (isTextMinLength(text)) {
            return Integer.parseInt(text);
        }
        return sumTokens(getTokens(text));
    }

    private String[] getTokens(final String text) {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER).matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITER);
    }

    private boolean isTextMinLength(String text) {
        return text.length() == MIN_TEXT_LENGTH;
    }

    private int sumTokens(final String[] tokens) {
        return Arrays.stream(tokens).mapToInt(this::parseNonNegative).sum();
    }

    private int parseNonNegative(String token) {
        int num = Integer.parseInt(token);
        if (num < 0) {
            throw new IllegalArgumentException("문자열 계산기에 음수를 전달할 수 없습니다: " + num);
        }
        return num;
    }
}
