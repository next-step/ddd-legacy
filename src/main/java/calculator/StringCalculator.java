package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n";
    private static final String CUSTOM_DELIMITER_REGEX_POSTFIX = "(.*)";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX + CUSTOM_DELIMITER_REGEX_POSTFIX);
    private final Splitter splitter;

    public StringCalculator() {
        this(Splitter.builder()
                     .with(new Delimiter(","))
                     .with(new Delimiter(":"))
                     .build());
    }

    private StringCalculator(Splitter splitter) {
        if (splitter == null) { throw new IllegalArgumentException(); }
        this.splitter = splitter;
    }

    public int add(final String text) {
        return text == null ? 0
                            : extendSplitterWithCustomDelimiter(text).split(text.replaceAll(CUSTOM_DELIMITER_REGEX,
                                                                                            ""))
                                                                     .sum();
    }

    private Splitter extendSplitterWithCustomDelimiter(final String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        return matcher.find() ? splitter.toBuilder()
                                        .with(new Delimiter(matcher.group(1)))
                                        .build()
                              : splitter;
    }
}
