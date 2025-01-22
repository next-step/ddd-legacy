package calculator;

import java.util.List;

public class StringValues {
    private final List<StringValue> values;

    public StringValues(List<StringValue> values) {
        this.values = values;
    }

    public static StringValues of(String str) {
        InputValue inputValue = InputValue.of(str);
        return new StringValues(inputValue.getNumbers());
    }

    public List<Integer> intValues() {
        return values.stream()
                .map(StringValue::toInt)
                .toList();
    }
}
