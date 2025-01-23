package calculator;

import java.util.List;

public class InputValue {
    private static final String DEFAULT_DELIMITER = ",:";
    private final CustomDelimiterExtractor customDelimiterExtractor;
    private final BodyValue body;

    public InputValue(CustomDelimiterExtractor customDelimiterExtractor, BodyValue body) {
        this.customDelimiterExtractor = customDelimiterExtractor;
        this.body = body;
    }

    public static InputValue of(String value) {
        String[] strings = value.split("\n");
        CustomDelimiterExtractor customDelimiterExtractor = new CustomDelimiterExtractor();
        BodyValue body = new BodyValue(strings[0]);
        if (strings.length > 1) {
            customDelimiterExtractor = new CustomDelimiterExtractor(strings[0]);
            body = new BodyValue(strings[1]);
        }
        body.validation(DEFAULT_DELIMITER + customDelimiterExtractor.getCustomDelimiter());
        return new InputValue(customDelimiterExtractor, body);
    }

    public List<PositiveInteger> getPositiveIntegers() {
        return body.getPositiveIntegers(DEFAULT_DELIMITER + customDelimiterExtractor.getCustomDelimiter());
    }
}
