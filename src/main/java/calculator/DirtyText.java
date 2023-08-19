package calculator;

import static calculator.ValidateUtils.checkEmpty;
import static calculator.ValidateUtils.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class DirtyText {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    @Nullable
    private final String value;

    public DirtyText(@Nullable final String value) {
        this.value = value;
    }

    public boolean isEmpty() {
        try {
            checkEmpty(value, "value");

            return true;
        } catch (final IllegalArgumentException ignored) {
            return false;
        }
    }

    public boolean isPositiveNumeric() {
        try {
            return 0 < Integer.parseInt(checkNotNull(value, "value"));
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public List<String> refine() {
        final Matcher matcher = CUSTOM_PATTERN.matcher(checkNotNull(value, "value"));
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);

            return Arrays.asList(matcher.group(2)
                .split(customDelimiter));
        }

        return Arrays.asList(value.split(",|:"));
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }
}
