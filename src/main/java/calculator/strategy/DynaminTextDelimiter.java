package calculator.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynaminTextDelimiter implements TextDelimiter {
    private static final String DEFAULT_DYNAMIC_PATTERN = "//(.)\\\\n(.*)";
    private final String dynamicPattern;

    public DynaminTextDelimiter() {
        this(DEFAULT_DYNAMIC_PATTERN);
    }

    public DynaminTextDelimiter(final String dynamicPattern) {
        this.dynamicPattern = dynamicPattern;
    }

    public boolean isSupport(final String text) {
        final Pattern pattern = Pattern.compile(dynamicPattern);
        final Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public List<String> split(final String text) {
        final Pattern pattern = Pattern.compile(dynamicPattern);
        final Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new RuntimeException("잘못된 호출입니다.");
        }
        final String delimiter = matcher.group(1);
        final String expression = matcher.group(2);
        return Arrays.stream(expression.split(delimiter))
                .toList();
    }
}
