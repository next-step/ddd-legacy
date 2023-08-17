package calculator.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PositiveNumberExtractor {

    public static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    public static final int CUSTOM_DELIMITER_SEQ = 1;
    public static final int NUMBERS_GROUP_SEQ = 2;
    private static final String DEFAULT_DELIMITERS = ",|:";

    public List<PositiveStringNumber> extractNumbers(String expression) {
        if (expression == null || expression.isBlank()) {
            return Collections.emptyList();
        }

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(expression);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_SEQ);
            return Arrays.stream(matcher.group(NUMBERS_GROUP_SEQ).split(customDelimiter))
                .map(PositiveStringNumber::of)
                .collect(Collectors.toList());
        }

        return Arrays.stream(expression.split(DEFAULT_DELIMITERS))
            .map(PositiveStringNumber::of)
            .collect(Collectors.toList());
    }
}
