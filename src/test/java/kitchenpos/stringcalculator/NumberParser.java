package kitchenpos.stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberParser {

    private static final ParsedNumber DEFAULT_PARSED_NUMBER = new ParsedNumber(0);
    public static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    public static final String COMMA_AND_COLON_REGEX = ",|:";
    private static final int EMPTY_NUMBER = 0;

    public static List<ParsedNumber> parse(final String text) {
        if (text == null || text.isBlank()) {
            return List.of(DEFAULT_PARSED_NUMBER);
        }

        return parseType(text);
    }

    private static List<ParsedNumber> parseType(final String text) {
        if (isSingleNumber(text)) {
            return List.of(new ParsedNumber(text));
        }

        return parseTypeByRegex(text);
    }

    private static List<ParsedNumber> parseTypeByRegex(final String text) {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.stream(tokens)
                    .map(ParsedNumber::new)
                    .collect(Collectors.toList());
        }

        String[] tokens = text.split(COMMA_AND_COLON_REGEX);

        validateSplitValue(tokens);

        return Arrays.stream(tokens)
                .map(ParsedNumber::new)
                .collect(Collectors.toList());
    }

    private static void validateSplitValue(String[] values) {
        if (values.length == EMPTY_NUMBER) {
            throw new IllegalArgumentException();
        }
    }

    private static boolean isSingleNumber(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
