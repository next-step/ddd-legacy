package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextSeparator {
    private static final String DEFAULT_SEPARATOR = ",|:";
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int NUMBER_INDEX = 2;
    private static final Pattern PATTERN = Pattern.compile("//(.)\\n(.*)");

    private final String separator;
    private final List<Number> numbers;

    public TextSeparator(String text) {
        Matcher matcher = PATTERN.matcher(text);
        this.separator = findSeparator(matcher);
        this.numbers = splitNumbers(findNumberText(matcher, text));
    }

    private String findSeparator(Matcher matcher) {
        matcher.reset();
        if (matcher.find()) {
            return matcher.group(CUSTOM_SEPARATOR_INDEX);
        }
        return DEFAULT_SEPARATOR;
    }

    private List<Number> splitNumbers(String numberText) {
        return Stream.of(numberText.split(this.separator))
                .map(Number::new)
                .collect(Collectors.toList());
    }

    private String findNumberText(Matcher matcher, String defaultValue) {
        matcher.reset();
        if (matcher.find()) {
            return matcher.group(NUMBER_INDEX);
        }
        return defaultValue;
    }

    public String getSeparator() {
        return separator;
    }

    public List<Integer> getNumbers() {
        return numbers.stream()
                .map(Number::intValue)
                .collect(Collectors.toList());
    }
}
