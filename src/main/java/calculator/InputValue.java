package calculator;

import java.util.List;

public class InputValue {
    private static final String DEFAULT_DELIMITER = ",:";
    private final CustomDelimiterExtractor customDelimiterExtractor;
    private final PositiveIntegerExtractor positiveIntegerExtractor;

    public InputValue(CustomDelimiterExtractor customDelimiterExtractor, PositiveIntegerExtractor positiveIntegerExtractor) {
        this.customDelimiterExtractor = customDelimiterExtractor;
        this.positiveIntegerExtractor = positiveIntegerExtractor;
    }

    public static InputValue of(String value) {
        String[] splitStringValues = value.split("\n");
        validateSplitStringValuesLength(splitStringValues);
        String header = getHeaderString(splitStringValues);
        String body = getBodyString(splitStringValues);
        CustomDelimiterExtractor customDelimiterExtractor = CustomDelimiterExtractor.of(header);
        PositiveIntegerExtractor positiveIntegerExtractor = PositiveIntegerExtractor.of(body, DEFAULT_DELIMITER, customDelimiterExtractor.getCustomDelimiter());
        return new InputValue(customDelimiterExtractor, positiveIntegerExtractor);
    }

    private static void validateSplitStringValuesLength(String[] splitStringValues) {
        if (splitStringValues.length > 2) {
            throw new RuntimeException("//;\\n1;2;3 와 같은 형식으로 입력해주세요");
        }
    }

    private static String getHeaderString(String[] splitStringValues) {
        if (splitStringValues.length > 1) {
            return splitStringValues[0];
        }
        return "";
    }

    private static String getBodyString(String[] splitStringValues) {
        if (splitStringValues.length > 1) {
            return splitStringValues[1];
        }
        return splitStringValues[0];
    }

    public List<PositiveInteger> getPositiveIntegers() {
        return positiveIntegerExtractor.getPositiveIntegers(DEFAULT_DELIMITER + customDelimiterExtractor.getCustomDelimiter());
    }
}
