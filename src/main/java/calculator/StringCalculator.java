package calculator;

import static calculator.ValidateUtils.checkNotNull;

import java.util.List;
import org.springframework.lang.Nullable;

public class StringCalculator {

    public StringCalculator() {
    }

    public int add(@Nullable final String text) {
        final DirtyText dirtyText = new DirtyText(text);

        if (dirtyText.isEmpty()) {
            return 0;
        }

        if (dirtyText.isPositiveNumeric()) {
            return Integer.parseInt(dirtyText.getValue()
                .orElseThrow(() -> new IllegalStateException("dirtyText value is null")));
        }

        final List<String> refinedTokens = dirtyText.refine();

        checkHasNegativeInt(refinedTokens);

        return sum(refinedTokens);
    }

    private void checkHasNegativeInt(final List<String> tokens) {
        checkNotNull(tokens, "tokens");

        for (final String token : tokens) {
            if (Integer.parseInt(token) < 0) {
                throw new RuntimeException(
                    String.format("tokens have negative int. tokens: %s", tokens));
            }
        }
    }

    private int sum(final List<String> tokens) {
        checkNotNull(tokens, "tokens");

        int total = 0;
        for (final String token : tokens) {
            final int number = Integer.parseInt(token);

            total += number;
        }
        return total;
    }
}
