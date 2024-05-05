package calculator2;

public class Number {

    private final int number;

    private Number(int number) {
        if (number < 0) {
            throw new RuntimeException("숫자는 음수일 수 없습니다.");
        }
        this.number = number;
    }

    public static Number of(String value) {
        try {
            return new Number(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number: " + value);
        }
    }

    public int getNumber() {
        return number;
    }
}