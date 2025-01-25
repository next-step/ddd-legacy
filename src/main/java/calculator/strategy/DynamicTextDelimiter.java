package calculator.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicTextDelimiter implements TextDelimiter {
    private static final int DELIMITER_GROUP = 1;
    private static final int EXPRESSION_GROUP = 2;
    private static final String DEFAULT_DYNAMIC_PATTERN = "//(.)\\\\n(.*)";

    private final Pattern pattern;

    public DynamicTextDelimiter() {
        this(DEFAULT_DYNAMIC_PATTERN);
    }

    public DynamicTextDelimiter(final String dynamicPattern) {
        this(Pattern.compile(dynamicPattern));
    }

    public DynamicTextDelimiter(final Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean isSupport(final String text) {
        final Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public List<String> split(final String text) {
        final Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("[둥적 구분자 분석기] 잘못된 호출입니다. text: %s".formatted(text));
        }
        final String delimiter = matcher.group(DELIMITER_GROUP);
        final String expression = matcher.group(EXPRESSION_GROUP);
        return Arrays.stream(expression.split(delimiter))
                .toList();
    }
}
