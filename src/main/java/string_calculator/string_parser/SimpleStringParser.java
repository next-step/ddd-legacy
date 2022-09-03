package string_calculator.string_parser;

import java.util.ArrayList;
import java.util.List;
import string_calculator.NonNegativeLong;

public class SimpleStringParser implements StringParser {

    @Override
    public List<NonNegativeLong> parse(String string) {
        if (string == null || string.isBlank()) {
            return new ArrayList<>();
        }

        final String[] tokens = string.split("[,:]");

        final List<NonNegativeLong> result = new ArrayList<>();
        for (String token : tokens) {
            result.add(new NonNegativeLong(token));
        }
        return result;
    }
}
