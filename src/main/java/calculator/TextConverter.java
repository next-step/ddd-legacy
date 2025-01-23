package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TextConverter<T> {
    private final Function<String, T> converter;

    public TextConverter(Function<String, T> converter) {
        this.converter = converter;
    }

    public List<T> convertToList(String text, String delimiter) {
        return Arrays.stream(text.split(delimiter))
                .map(converter)
                .toList();
    }
}
