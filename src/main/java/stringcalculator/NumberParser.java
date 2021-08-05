package stringcalculator;

import java.util.stream.Collectors;

public class NumberParser {

    public static Numbers parse(final String text) {
        SplitTexts texts = StringSplitter.split(text);

        if (texts.isEmpty()) {
            return new Numbers(new Number(0));
        }

        return new Numbers(
            texts.getValues().stream().map(Number::new).collect(Collectors.toList())
        );
    }
}
