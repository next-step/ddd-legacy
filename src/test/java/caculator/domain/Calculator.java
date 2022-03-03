package caculator.domain;

import java.util.Objects;

public class Calculator {

    private Calculator() {
        throw new AssertionError();
    }

    public static int add(String stringNumbers) {
        if (cannotCalculate(stringNumbers)) {
            return 0;
        }

        return Numbers.from(stringNumbers).sum();
    }

    private static boolean cannotCalculate(String stringNumbers) {
        return Objects.isNull(stringNumbers) || stringNumbers.isEmpty();
    }
}
