package calculator;

public class PositiveNumber {

    private final int number;

    public PositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
