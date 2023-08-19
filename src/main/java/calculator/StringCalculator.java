package calculator;

import java.util.List;
import org.springframework.lang.Nullable;

public class StringCalculator {

    private final Refiner refiner;

    public StringCalculator() {
        this.refiner = new DefaultRefiner();
    }

    /**
     * @throws IllegalStateException 계산기의 시나리오가 지켜지지 않았을 때
     */
    public int add(@Nullable final String text) {
        final DirtyText dirtyText = new DirtyText(text, refiner);

        if (dirtyText.isEmpty()) {
            return 0;
        }

        if (dirtyText.isPositiveNumeric()) {
            return Integer.parseInt(dirtyText.getValue()
                .orElseThrow(
                    () -> new IllegalStateException("illegal scenario. dirtyText value is null")));
        }

        final List<String> refinedTokens = dirtyText.refine();

        checkHasNegativeInt(refinedTokens);

        return sum(refinedTokens);
    }

    private void checkHasNegativeInt(final List<String> tokens) {
        for (final String token : tokens) {
            if (Integer.parseInt(token) < 0) {
                throw new RuntimeException(
                    String.format("tokens have negative int. tokens: %s", tokens));
            }
        }
    }

    private int sum(final List<String> tokens) {
        int total = 0;
        for (final String token : tokens) {
            final int number = Integer.parseInt(token);

            total += number;
        }
        return total;
    }
}
