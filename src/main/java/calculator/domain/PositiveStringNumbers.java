package calculator.domain;

import java.util.Collections;
import java.util.List;

public class PositiveStringNumbers {

    public static final PositiveStringNumbers EMPTY_POSITIVE_STRING_NUMBERS = new PositiveStringNumbers(Collections.emptyList());
    private final List<PositiveStringNumber> values;

    public PositiveStringNumbers(List<PositiveStringNumber> values) {
        this.values = values;
    }

    public PositiveStringNumber addAll() {
        return values.stream()
            .reduce(PositiveStringNumber::add)
            .orElse(PositiveStringNumber.ZERO);
    }

    public List<PositiveStringNumber> getValues() {
        return Collections.unmodifiableList(values);
    }
}
