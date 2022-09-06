package string_calculator.string_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import string_calculator.NonNegativeLong;

public class SimpleStringParser implements StringParser {

    @Override
    public List<NonNegativeLong> parse(String string) {
        if (string == null || string.isBlank()) {
            return new ArrayList<>();
        }

        final String[] tokens = string.split("[,:]");

        return Arrays.stream(tokens)
                .map(NonNegativeLong::new)
                .collect(Collectors.toUnmodifiableList());
    }
}
