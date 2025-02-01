package stringcalculator;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringNumberParser {

    public static String[] parse(String input, List<String> separators) {
        String separatorSetString = separators.stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|"));

        return input.split(separatorSetString);
    }
}
