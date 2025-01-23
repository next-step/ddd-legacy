package calculator;

public class InputText {

    private String delimiters = ",:";
    private final String numberText;

    private static final String CUSTOM_DELIMITER_START = "//";
    private static final String CUSTOM_DELIMITER_END = "\\n";


    public InputText(String text) {
        numberText = extractCustomDelimiter(text);
    }

    private String extractCustomDelimiter(String text) {
        String str = text;
        if (hasCustomDelimiter(text)) {
            delimiters += text.charAt(2);
            str = text.substring(5);
        }
        return str;
    }

    private boolean hasCustomDelimiter(String text) {
        return text.length() > 5
                && text.startsWith(CUSTOM_DELIMITER_START)
                && CUSTOM_DELIMITER_END.equals(text.substring(3, 5));
    }

    public String getDelimiters() {
        return delimiters;
    }

    public String getNumberText() {
        return numberText;
    }

}
