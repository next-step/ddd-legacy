package calculator;

import org.springframework.lang.Nullable;

public class StringCalculator {

    private final Refiner refiner;

    public StringCalculator() {
        this.refiner = new DefaultRefiner();
    }

    public int add(@Nullable final String text) {
        final DirtyText dirtyText = new DirtyText(text, refiner);

        if (dirtyText.isEmpty()) {
            return 0;
        }

        final PositiveNumbers numbers = dirtyText.refine();

        return numbers.sum();
    }
}
