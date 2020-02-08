package calculator;

public class PositiveNumber {

    private int value;

    private PositiveNumber(int value) {
        if (value < 0) {
            throw new RuntimeException();
        }
        this.value = value;
    }

    public static PositiveNumber of(String number) {
        return new PositiveNumber(Integer.parseInt(number));
    }

    public static PositiveNumber of(int number) {
        return new PositiveNumber(number);
    }

    public void sum(PositiveNumber number) {
        this.value += number.value;
    }

    public int getValue() {
        return value;
    }
}
