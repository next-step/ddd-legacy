package stringcalculator;

import java.util.Objects;
import java.util.stream.Stream;

public class StringCalculator {

    private static final String SEPARATOR = ",|:";

    public StringCalculator() {
    }

    public int sum(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new RuntimeException("Null 이거나 공란일 수 없습니다.");
        }
        return Stream.of(value.split(SEPARATOR))
                .map(PositiveNumber::new)
                .mapToInt(PositiveNumber::toInt)
                .sum();
    }
}
