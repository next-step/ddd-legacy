package calculator;

public class SimpleCustomDelimiterExtractor implements CustomDelimiterExtractor {

    public static final String DEFAULT_DELIMITERS = ",:";
    private static final String EMPTY_STRING = "";

    private static final String CUSTOM_DELIMITER_START = "//";
    private static final String CUSTOM_DELIMITER_END = "\\n";
    private static final int CUSTOM_DELIMITER_POSITION = 2;
    private static final int CUSTOM_DELIMITER_END_POSITION = 5;


    @Override
    public InputText extractDelimiter(String text) {
        if (text == null || text.isBlank()) {
            return new InputText(EMPTY_STRING, DEFAULT_DELIMITERS);
        }

        if (hasCustomDelimiter(text)) {
            return new InputText(text.substring(CUSTOM_DELIMITER_END_POSITION), DEFAULT_DELIMITERS + text.charAt(CUSTOM_DELIMITER_POSITION));
        }
        return new InputText(text, DEFAULT_DELIMITERS);
    }

    private boolean hasCustomDelimiter(String text) {
        return text.length() > CUSTOM_DELIMITER_END_POSITION
                && text.startsWith(CUSTOM_DELIMITER_START)
                && CUSTOM_DELIMITER_END.equals(text.substring(CUSTOM_DELIMITER_POSITION + 1, CUSTOM_DELIMITER_END_POSITION));
    }

}
