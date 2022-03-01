package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TextConverter {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int MATCHING_PART = 1;
    private static final int DELIMITER_FORMULA = 2;

    private TextConverter() {
    }

    public static PositiveNumbers convertToNumbers(String text, String delimiter) {
        if (Objects.isNull(text) || text.trim().isEmpty()) {
            return new PositiveNumbers();
        }
        final String[] splitText = text.split(delimiter);

        final Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            return getNumbersWithCustomDelimiter(matcher);
        }
        List<PositiveNumber> numbers = getNumbersWithNoCustomDelimiter(splitText);
        return new PositiveNumbers(numbers);
    }

    private static PositiveNumbers getNumbersWithCustomDelimiter(Matcher matcher) {
        final String delimiter = matcher.group(MATCHING_PART);
        final List<PositiveNumber> numbers = Stream.of(matcher.group(DELIMITER_FORMULA).split(delimiter))
                .map(String::trim)
                .map(TextConverter::convertToPositiveNumber)
                .collect(toList());
        return new PositiveNumbers(numbers);
    }

    private static List<PositiveNumber> getNumbersWithNoCustomDelimiter(String[] splitText) {
        return Arrays.stream(splitText)
                .map(TextConverter::convertToPositiveNumber)
                .collect(toList());
    }

    private static PositiveNumber convertToPositiveNumber(String text) {
        try {
            final int number = Integer.parseInt(text);
            return new PositiveNumber(number);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return new PositiveNumber();
    }
}
