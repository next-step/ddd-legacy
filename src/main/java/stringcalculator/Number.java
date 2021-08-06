package stringcalculator;

public class Number {

    private static final int LOWER_BOUND = 0;
    private static final String NEGATIVE_NOT_ALLOWED_MESSAGE = "음수를 입력할 수 없습니다.";

    private final int number;

    public Number(int number) {
        numberShouldNotBeNegative(number);
        this.number = number;
    }

    public Number(String number) {
        this(Integer.parseInt(number));
    }

    private void numberShouldNotBeNegative(final int number) {
        if (number < LOWER_BOUND) {
            throw new IllegalArgumentException(NEGATIVE_NOT_ALLOWED_MESSAGE);
        }
    }

    public int getValue() {
        return this.number;
    }
}
