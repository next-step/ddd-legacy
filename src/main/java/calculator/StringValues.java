package calculator;

import java.util.List;
import java.util.stream.Stream;

public class StringValues {
    private static final String DEFAULT_DELIMITER_REGEX = "[,:]";
    private final List<StringValue> values;

    public StringValues(String str, DefaultStringValuesValidator defaultStringValuesValidator) {
        defaultStringValuesValidator.validation(str, DEFAULT_DELIMITER_REGEX);
        String[] strings = str.split(DEFAULT_DELIMITER_REGEX);
        this.values = Stream.of(strings)
                .map(StringValue::of)
                .toList();
    }

    public List<Integer> intValues() {
        return values.stream()
                .map(StringValue::toInt)
                .toList();
    }
}
