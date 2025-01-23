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
        String[] strings = value.split("\n");
        String header = "";
        String body = strings[0];
        if (strings.length > 2) {
            throw new RuntimeException("//;\\n1;2;3 와 같은 형식으로 입력해주세요");
        }
        if (strings.length > 1) {
            header = strings[0];
            body = strings[1];
        }
        CustomDelimiterExtractor customDelimiterExtractor = CustomDelimiterExtractor.of(header);
        PositiveIntegerExtractor positiveIntegerExtractor = PositiveIntegerExtractor.of(body, DEFAULT_DELIMITER, customDelimiterExtractor.getCustomDelimiter());
        return new InputValue(customDelimiterExtractor, positiveIntegerExtractor);
    }

    public List<PositiveInteger> getPositiveIntegers() {
        return positiveIntegerExtractor.getPositiveIntegers(DEFAULT_DELIMITER + customDelimiterExtractor.getCustomDelimiter());
    }
}
