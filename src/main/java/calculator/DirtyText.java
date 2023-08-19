package calculator;

import static calculator.ValidateUtils.checkEmpty;
import static calculator.ValidateUtils.checkNotNull;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public final class DirtyText {

    @Nullable
    private final String value;
    private final Refiner refiner;

    public DirtyText(@Nullable final String value, final Refiner refiner) {
        this.value = value;
        this.refiner = checkNotNull(refiner, "refiner");
    }

    public boolean isEmpty() {
        try {
            checkEmpty(value, "value");

            return true;
        } catch (final IllegalArgumentException ignored) {
            return false;
        }
    }

    /**
     * @throws IllegalArgumentException {@link #value}가 null일 때
     */
    public boolean isPositiveNumeric() {
        checkNotNull(value, "value");

        try {
            return 0 < Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    /**
     * @throws IllegalArgumentException {@link #value}가 null일 때
     */
    public List<String> refine() {
        checkNotNull(value, "value");

        return refiner.execute(value);
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }
}
