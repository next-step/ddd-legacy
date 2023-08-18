package calculator;

import static calculator.ValidateUtils.checkNotEmpty;
import static calculator.ValidateUtils.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

public class StringCalculator {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    public StringCalculator() {
    }

    public int add(@Nullable final String text) {
        if (isEmpty(text)) {
            return 0;
        }

        if (isPositiveNumeric(text)) {
            return Integer.parseInt(text);
        }

        final String[] tokens = parse(text);

        checkHasNegativeInt(tokens);

        return sum(tokens);
    }


    private boolean isEmpty(@Nullable final String text) {
        return StringUtils.isEmpty(text);
    }

    private boolean isPositiveNumeric(final String text) {
        checkNotEmpty(text, "text");

        try {
            return 0 < Integer.parseInt(text);
        } catch (final NumberFormatException e) {
            return false;
        }
    }


    private String[] parse(final String rawText) {
        checkNotEmpty(rawText, "rawText");

        final Matcher matcher = CUSTOM_PATTERN.matcher(rawText);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }

        return rawText.split(",|:");
    }

    private void checkHasNegativeInt(final String[] tokens) {
        checkNotNull(tokens, "tokens");

        for (final String token : tokens) {
            if (Integer.parseInt(token) < 0) {
                throw new RuntimeException();
            }
        }
    }

    private int sum(final String[] tokens) {
        checkNotNull(tokens, "tokens");

        int total = 0;
        for (final String token : tokens) {
            final int number = Integer.parseInt(token);

            total += number;
        }
        return total;
    }
}
