package stringcalculator;

public class PositiveNumber {
    private static final int ZERO = 0;
    private final int number;

    public PositiveNumber(int number) {
        if (number < ZERO) {
            throw new RuntimeException("음수는 불가능합니다.");
        }
        this.number = number;
    }

    public static PositiveNumber of(String numberText) {
        if (numberText == null || numberText.isBlank()) {
            return zero();
        }
        int number = Integer.parseInt(numberText);
        return new PositiveNumber(number);
    }

    public static PositiveNumber zero() {
        return new PositiveNumber(ZERO);
    }

    public PositiveNumber add(PositiveNumber positiveNumber) {
        return new PositiveNumber(positiveNumber.number + this.number);
    }

    public int getNumber() {
        return number;
    }
}
