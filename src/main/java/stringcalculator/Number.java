package stringcalculator;

public class Number {

    private final int number;

    public Number(int number) {
        numberShouldNotBeNegative(number);
        this.number = number;
    }

    public Number(String number) {
        this(Integer.parseInt(number));
    }

    private void numberShouldNotBeNegative(final int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수를 입력할 수 없습니다.");
        }
    }

    public int getValue() {
        return this.number;
    }
}
