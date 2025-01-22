package calculator;

public class HeaderValue {
    private final String header;
    public HeaderValue() {
        this.header = null;
    }

    public HeaderValue(String header) {
        if (header != null && !header.matches("^//[^0-9]$")) {
            throw new RuntimeException("커스텀 구분자의 형식이 아닙니다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용합니다.");
        }
        this.header = header;
    }

    public String getCustomDelimiter() {
        if (header != null) {
            return header.substring(2);
        }

        return "";
    }
}
