package calculator;

public class PositiveInteger {

    private Integer value;

    public PositiveInteger(Integer value) {
        validatePositiveIntegerNumber(value);
        this.value = value;
    }

    public Integer valueOf() {
        return this.value;
    }

    private void validatePositiveIntegerNumber(Integer value) {
        if (value <= 0 || value > Integer.MAX_VALUE) {
            throw new RuntimeException();
        }
    }
}
