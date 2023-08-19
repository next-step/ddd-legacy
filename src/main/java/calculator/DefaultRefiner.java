package calculator;

import static calculator.ValidateUtils.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultRefiner implements Refiner {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    @Override
    public List<String> execute(final String text) {
        checkNotNull(text, "text");

        final Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);

            return Arrays.asList(matcher.group(2)
                .split(customDelimiter));
        }

        return Arrays.asList(text.split(",|:"));
    }
}
