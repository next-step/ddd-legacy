package calculator.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixedTextDelimiter implements TextDelimiter {
    private static final String DEFAULT_FIXED_DELIMITER = ",|:";
    private final String delimiter;

    public FixedTextDelimiter() {
        this(DEFAULT_FIXED_DELIMITER);
    }

    public FixedTextDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public boolean isSupport(final String text) {
        final Pattern pattern = Pattern.compile(delimiter);
        final Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    @Override
    public List<String> split(final String text) {
        final Pattern pattern = Pattern.compile(text);
        final Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new RuntimeException("잘못된 호출입니다.");
        }
        return Arrays.stream(text.split(delimiter))
                .toList();
    }
}
