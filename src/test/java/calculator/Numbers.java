package calculator;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class Numbers {

    private final List<Number> values;

    static Numbers parse(final List<String> values) {
        requireNonNull(values);

        return new Numbers(values.stream()
            .map(Number::parse)
            .collect(Collectors.toList()));
    }

    private Numbers(final List<Number> values) {
        requireNonNull(values);

        this.values = Collections.unmodifiableList(values);
    }

    int add() {
        return values.stream()
            .reduce(Number::add)
            .map(Number::getValue)
            .orElse(0);
    }
}
