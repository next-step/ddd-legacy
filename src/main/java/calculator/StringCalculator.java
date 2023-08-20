package calculator;

import org.springframework.util.StringUtils;

public class StringCalculator {

    private static final String CUSTOM_DELIMITER_START = "//";
    private static final String CUSTOM_DELIMITER_END = "\n";
    private static final String BLANK_CUSTOM_DELIMITER = "";

    public int add(final String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }

        if (isNumber(text) && Integer.parseInt(text) > 0) {
            return Integer.parseInt(text);
        }

        String customDelimiter = getCustomDelimiter(text);
        String customRemovedText = removeCustomDelimiter(text);

        PositiveInteger positiveInteger = new PositiveInteger(customRemovedText, ",|:|" + customDelimiter);
        return positiveInteger.sum();
    }

    private String getCustomDelimiter(String text) {
        if (!text.startsWith(CUSTOM_DELIMITER_START)) {
            return BLANK_CUSTOM_DELIMITER;
        }
        int endDelimiterIdx = text.indexOf(CUSTOM_DELIMITER_END);
        return text.substring(CUSTOM_DELIMITER_START.length(), endDelimiterIdx);
    }

    private String removeCustomDelimiter(String text) {
        if (!text.startsWith(CUSTOM_DELIMITER_START)) {
            return text;
        }
        int endDelimiterIdx = text.indexOf(CUSTOM_DELIMITER_END);
        return text.substring(endDelimiterIdx + 1);
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
