package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

final class TokenFactory {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    private static final String DEFAULT_DELIMITERS;

    static {
        final List<Delimiter> defaultDelimiters = new ArrayList<>();
        defaultDelimiters.add(Delimiter.COMMA);
        defaultDelimiters.add(Delimiter.COLON);

        DEFAULT_DELIMITERS = defaultDelimiters.stream()
            .map(Delimiter::getValue)
            .collect(Collectors.joining("|"));
    }

    List<String> createTokens(@Nullable final String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        final String[] rawTokens;

        final Matcher customDelimiterMatcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (customDelimiterMatcher.find()) {
            final String customDelimiter = customDelimiterMatcher.group(1);
            final String refinedText = customDelimiterMatcher.group(2);

            rawTokens = refinedText.split(new Delimiter(customDelimiter).getValue());
        } else {
            rawTokens = text.split(DEFAULT_DELIMITERS);
        }

        return Collections.unmodifiableList(Arrays.asList(rawTokens));
    }
}
