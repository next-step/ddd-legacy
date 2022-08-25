package calculator;

import java.util.Arrays;
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

        if (Objects.equals(this.value, "")) {
            return 0;
        }

        String[] numbers = this.value.split(",");

        return Arrays.stream(numbers).mapToInt(Integer::parseInt).sum();
    }

}
