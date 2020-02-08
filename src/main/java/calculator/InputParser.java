package calculator;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InputParser {
    private static final String DEFAULT_DELIMITERS_REGEX = ",|:";
    private static final String CUSTOM_DELIMITER_MATCHING_REGEX = "^//(.)\\\\n(.+)$";

    static List<Integer> parseToInts(String text) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }

        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_MATCHING_REGEX).matcher(text);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);
            final String extractedText = matcher.group(2);
            return splitToIntegers(extractedText, customDelimiter);
        }

        return splitToIntegers(text, DEFAULT_DELIMITERS_REGEX);
    }

    private static List<Integer> splitToIntegers(String text, String delimiter) {
        return Stream.of(text.split(delimiter))
                .map(InputParser::parseToInteger)
                .collect(Collectors.toList());
    }

    private static Integer parseToInteger(String character) {
        try {
            return Integer.parseInt(character);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Can not parse integer - error: %s", e.getMessage()));
        }
    }

}
