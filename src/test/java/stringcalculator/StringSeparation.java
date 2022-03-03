package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringSeparation {

    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DELIMITER = "[,:]";

    public List<Number> separate(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.singletonList(Number.ZERO);
        }

        Matcher matcher = PATTERN.matcher(text);

        if (matcher.find()) {
            return separateByCustomDelimiter(matcher);
        }

        return separateByDefaultDelimiter(text);
    }

    private List<Number> separateByCustomDelimiter(Matcher matcher) {
        String customDelimiter = matcher.group(1);

        return Arrays.stream(matcher.group(2).split(customDelimiter))
                .map(Number::new)
                .collect(Collectors.toList());
    }

    private List<Number> separateByDefaultDelimiter(String text) {
        return Arrays.stream(text.split(DELIMITER))
                .map(Number::new)
                .collect(Collectors.toList());
    }
}
