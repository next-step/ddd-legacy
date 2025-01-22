package calculator;

import java.util.List;

public class InputValue {
    private static final String DEFAULT_DELIMITER = ",:";
    private final HeaderValue header;
    private final BodyValue body;

    public InputValue(HeaderValue header, BodyValue body) {
        this.header = header;
        this.body = body;
    }

    public static InputValue of(String value) {
        String[] strings = value.split("\n");
        HeaderValue header = new HeaderValue();
        BodyValue body = new BodyValue(strings[0]);
        if (strings.length > 1) {
            header = new HeaderValue(strings[0]);
            body = new BodyValue(strings[1]);
        }
        body.validation(DEFAULT_DELIMITER + header.getCustomDelimiter());
        return new InputValue(header, body);
    }

    public List<StringValue> getNumbers() {
        return body.getNumbers(DEFAULT_DELIMITER + header.getCustomDelimiter());
    }
}
