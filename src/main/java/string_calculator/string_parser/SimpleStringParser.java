package string_calculator.string_parser;

import java.util.ArrayList;
import java.util.List;

public class SimpleStringParser implements StringParser {

    @Override
    public List<Long> parse(String string) {
        if (string == null || string.isBlank()) {
            return new ArrayList<>();
        }

        final String[] tokens = string.split("[,:]");

        final List<Long> result = new ArrayList<>();
        for (String token : tokens) {
            result.add(Long.valueOf(token));
        }
        return result;
    }
}
