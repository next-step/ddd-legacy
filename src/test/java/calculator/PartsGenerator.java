package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PartsGenerator {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final List<String> delimiters = new ArrayList<>(List.of(",", ":"));

    public Parts generate(String input) {
        input = addCustomDelimiter(input);
        String[] parts = split(input);
        return new Parts(parts);
    }

    private String addCustomDelimiter(String input) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.find()) {
            delimiters.add(matcher.group(1));
            return matcher.group(2);
        }
        return input;
    }

    private String[] split(String input) {
        return input.split(String.join("|", delimiters));
    }
}
