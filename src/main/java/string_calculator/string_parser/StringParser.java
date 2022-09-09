package string_calculator.string_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import string_calculator.NonNegativeLong;

public abstract class StringParser {

    public List<NonNegativeLong> parse(final String string) {
        if (string == null || string.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(this.tokens(string))
                .map(NonNegativeLong::new)
                .collect(Collectors.toUnmodifiableList());
    }

    protected abstract String[] tokens(final String string);
}
