package stringCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringPatternParse {

    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final String DEFAULT_SEPARATOR = ",|:";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMITER);
    private static final Pattern DEFAULT_PATTERN = Pattern.compile(DEFAULT_SEPARATOR);

    private static Matcher matcher;

    public List<PositiveNumber> parseStringPatternToPositiveNumberList(String text) {

        matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find())
            return parseCustomPattern(text);

        return parseDefaultPattern(text);
    }

    private List<PositiveNumber> parseCustomPattern(String text) {
        return Arrays.stream(
                matcher.group(2).split(matcher.group(1))
        )
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    private List<PositiveNumber> parseDefaultPattern(String text) {
        return Arrays.stream(
                text.split(DEFAULT_SEPARATOR)
        )
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

}
