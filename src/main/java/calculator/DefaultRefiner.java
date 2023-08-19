package calculator;

import static calculator.ValidateUtils.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultRefiner implements Refiner {

    private static final String DEFAULT_REGEX = ",|:";
    private static final String CUSTOM_REGEX = "//(.)\n(.*)";

    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_REGEX);

    @VisibleForTesting
    Pattern getCustomPattern() {
        return CUSTOM_PATTERN;
    }

    @VisibleForTesting
    String getDefaultRegex() {
        return DEFAULT_REGEX;
    }

    @Override
    public List<String> execute(final String text) {
        checkNotNull(text, "text");

        final Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);

            return Arrays.asList(matcher.group(2)
                .split(customDelimiter));
        }

        return Arrays.asList(text.split(DEFAULT_REGEX));
    }
}
