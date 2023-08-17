package stringcalculator;

import java.util.Objects;

public class Positive {
    private final int number;

    public Positive(String text) {
        this.number = Integer.parseInt(text);
        if (this.number < 0) {
            throw new RuntimeException(StringCalculatorExceptionMessage.IS_NEGATIVE.getMessage());
        }
    }

    public int parseInt() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Positive positive = (Positive) o;
        return number == positive.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
