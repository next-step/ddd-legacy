package calculator;

import static calculator.ValidateUtils.checkNotNull;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultRefiner implements Refiner {

    private static final String DEFAULT_REGEX = ",|:";
    private static final String CUSTOM_REGEX = "//(.)\n(.*)";

    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_REGEX);

    @Override
    public Numbers execute(final String text) {
        checkNotNull(text, "text");

        final Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);

            return new Numbers(
                Arrays.stream(matcher.group(2)
                        .split(customDelimiter))
                    .map(token -> new Number(Integer.parseInt(token)))
                    .collect(ImmutableList.toImmutableList()));
        }

        return new Numbers(
            Arrays.stream(text.split(DEFAULT_REGEX))
                .map(token -> new Number(Integer.parseInt(token)))
                .collect(ImmutableList.toImmutableList()));
    }
}
