package calculator;

public class PositiveNumber {
    private final int number;

    public PositiveNumber(int number) {
        this.number = number;
    }

    public PositiveNumber(String stringNumber) {
        if (!isPositiveNumber(stringNumber)) {
            throw new RuntimeException("음수를 입력함");
        }

        try {
            this.number = Integer.parseInt(stringNumber.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아님");
        }
    }

    private boolean isPositiveNumber(String stringNumber) {
        return !stringNumber.startsWith("-");
    }

    public PositiveNumber plus(PositiveNumber other) {
        return new PositiveNumber(this.number + other.number);
    }

    public int getNumber() {
        return this.number;
    }
}
