package calculator;

import java.util.Objects;

public class StringCalculator {

    private final String value;

    private StringCalculator(String value) {
        this.value = value;
    }

    public static StringCalculator of(String value) {
        return new StringCalculator(value);
    }

    public int calculate() {
        if (Objects.equals(value, "")) {
            return 0;
        }
        return 1;
    }

}
