package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculatorTokenParser {
    private final int DELIMITER_GROUP_INDEX = 1;
    private final int TEXT_GROUP_INDEX = 2;

    private final String DEFAULT_DELIMITER_PATTERN = "[,|:]";
    private final String CUSTOM_DELIMITER_PATTERN = "//(.*)\n(.*)";

    private final Pattern pattern;


    public StringCalculatorTokenParser() {
        pattern = Pattern.compile(CUSTOM_DELIMITER_PATTERN);
    }

    public List<NonNegativeInteger> getIntegerTokens(String text) {
        Matcher matcher = pattern.matcher(text);

        String[] tokens = matcher.find() ?
                matcher.group(TEXT_GROUP_INDEX).split(Pattern.quote(matcher.group(DELIMITER_GROUP_INDEX))) : text.split(DEFAULT_DELIMITER_PATTERN);

        return Arrays.stream(tokens)
                .map(token -> NonNegativeInteger.of(token))
                .collect(Collectors.toList());

    }
}
