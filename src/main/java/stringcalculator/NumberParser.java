package stringcalculator;

import java.util.stream.Collectors;

public class NumberParser {

    private static final Numbers EMPTY_NUMBERS = new Numbers();

    public static Numbers parse(final String text) {
        SplitTexts texts = StringSplitter.split(text);

        if (texts.isEmpty()) {
            return EMPTY_NUMBERS;
        }

        return new Numbers(
            texts.getValues().stream().map(Number::new).collect(Collectors.toList())
        );
    }
}
