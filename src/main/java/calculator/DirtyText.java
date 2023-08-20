package calculator;

import static calculator.ParameterValidateUtils.checkNotNull;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public final class DirtyText {

    @Nullable
    private final String value;
    private final Refiner refiner;

    public DirtyText(@Nullable final String value, final Refiner refiner) {
        this.value = value;
        this.refiner = checkNotNull(refiner, "refiner");
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }

    /**
     * @throws IllegalArgumentException {@link #value}가 null일 때
     */
    public PositiveNumbers refine() {
        return refiner.execute(value);
    }
}
