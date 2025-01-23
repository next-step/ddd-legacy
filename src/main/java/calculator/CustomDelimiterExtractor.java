package calculator;

public class CustomDelimiterExtractor {
    private final String originalValue;
    public CustomDelimiterExtractor() {
        this.originalValue = null;
    }

    public CustomDelimiterExtractor(String originalValue) {
        if (originalValue != null && !originalValue.matches("^//[^0-9]$")) {
            throw new RuntimeException("커스텀 구분자의 형식이 아닙니다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용합니다.");
        }
        this.originalValue = originalValue;
    }

    public String getCustomDelimiter() {
        if (originalValue != null) {
            return originalValue.substring(2);
        }

        return "";
    }
}
