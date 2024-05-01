package calculator;

public class Number {

    public static final int ZERO_NUMBER = 0;
    private final int number;

    public Number(String token) {
        this(Integer.parseInt(token));
    }

    public Number(int number) {
        if (number < 0) {
            throw new RuntimeException(number + "is Negative");
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
