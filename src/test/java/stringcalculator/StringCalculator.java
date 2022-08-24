package stringcalculator;

import java.util.Objects;

public class StringCalculator {

    private static final String SEPARATOR = ",|:";

    public StringCalculator() {
    }

    public int sum(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new RuntimeException("Null 이거나 공란일 수 없습니다.");
        }

        for (String stringNumber : value.split(SEPARATOR)) {
            PositiveNumber number = new PositiveNumber(stringNumber);
        }
        return 0;
    }
}
