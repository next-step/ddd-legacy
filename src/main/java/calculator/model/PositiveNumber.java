package calculator.model;

public class PositiveNumber {
    public int value;

    public PositiveNumber(int value) {
        this.value = value;
        this.validate(value);
    }

    public PositiveNumber(String str) {
        this(Integer.parseInt(str));
    }

    public int getValue() {
        return this.value;
    }

    private void validate(int value) {
        if (value < 0) {
            throw new RuntimeException("Number must be positive");
        }
    }

    public PositiveNumber sum(PositiveNumber num) {
        return new PositiveNumber(this.getValue() + num.getValue());
    }
}
