package calculator;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DefaultStringSplitterMatcher implements StringSplitterMatcher {

    private static final String CUSTOM_SEPARATOR_PATTERN = "//(.)\\n(.*)";
    private static final Map<Pattern, Function<Matcher, StringSplitter>> SPLITTER_PER_PATTERN_MAP = Map.of(
        Pattern.compile(CUSTOM_SEPARATOR_PATTERN),
        (matcher) -> new CustomStringSplitter(matcher.group(2), matcher.group(1))
    );

    @Override
    public StringSplitter match(final String value) {
        return SPLITTER_PER_PATTERN_MAP.entrySet()
            .stream()
            .filter(splitterCreator ->
                value != null && value.matches(splitterCreator.getKey().pattern()))
            .findAny()
            .map(splitterCreator -> {
                final Matcher matcher = splitterCreator.getKey().matcher(value);
                return matcher.find()
                    ? splitterCreator.getValue().apply(matcher)
                    : new DefaultStringSplitter(value);
            })
            .orElse(new DefaultStringSplitter(value));
    }
}
