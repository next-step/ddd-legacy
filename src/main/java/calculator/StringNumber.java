package calculator;

public class StringNumber {
    private static final int MINIMUM_NUMBER = 0;

    private final int number;

    public StringNumber() {
        this(0);
    }

    public StringNumber(String number) {
        this(Integer.parseInt(number));
    }

    public StringNumber(int number) {
        if (number < MINIMUM_NUMBER) {
            throw new RuntimeException("0이상의 숫자를 입력해주세요.");
        }

        this.number = number;
    }

    public StringNumber add(StringNumber other) {
        return new StringNumber(this.number + other.number);
    }

    public int getNumber() {
        return number;
    }
}
