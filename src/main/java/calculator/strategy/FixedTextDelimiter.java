package calculator.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixedTextDelimiter implements TextDelimiter {
    private static final String DEFAULT_FIXED_DELIMITER = ",|:";
    private final Pattern pattern;

    public FixedTextDelimiter() {
        this(DEFAULT_FIXED_DELIMITER);
    }

    public FixedTextDelimiter(final String delimiter) {
        this(Pattern.compile(delimiter));
    }

    private FixedTextDelimiter(final Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isSupport(final String text) {
        final Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    @Override
    public List<String> split(final String text) {
        final Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("[고정 구분자 분석기] 잘못된 호출입니다. text: %s".formatted(text));
        }
        final String delimiter = pattern.pattern();
        return Arrays.stream(text.split(delimiter))
                .toList();
    }
}
