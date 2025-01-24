package calculator;

import java.util.regex.Pattern;

public class CustomDelimiterExtractor {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("^//[^0-9]$");
    private final String originalValue;

    public CustomDelimiterExtractor(String originalValue) {
        this.originalValue = originalValue;
    }

    public static CustomDelimiterExtractor of(String originalValue) {
        if (originalValue == null || originalValue.isEmpty()) {
            return new CustomDelimiterExtractor(null);
        }
        if (!CUSTOM_DELIMITER_PATTERN.matcher(originalValue).matches()) {
            throw new RuntimeException("커스텀 구분자의 형식이 아닙니다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용합니다.");
        }
        return new CustomDelimiterExtractor(originalValue);
    }

    public String getCustomDelimiter() {
        if (originalValue != null) {
            return originalValue.substring(2);
        }

        return "";
    }
}
