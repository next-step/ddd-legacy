package calculator;

public class PositiveNumber {
    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private final int number;

    public PositiveNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("입력 숫자는 양수여야 합니다.");
        }
        this.number = number;
    }

    public PositiveNumber sum(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.number + positiveNumber.number);
    }

    public int getNumber() {
        return this.number;
    }
}
