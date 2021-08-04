package stringcalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NumberParser {

    public static List<Number> parse(final String text) {
        List<String> numberStrings = StringSplitter.split(text);

        if (numberStrings.isEmpty()) {
            return new ArrayList<>(Collections.singletonList(new Number(0)));
        }

        return numberStrings.stream().map(Number::new).collect(Collectors.toList());
    }
}
