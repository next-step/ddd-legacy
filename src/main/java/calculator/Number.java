package calculator;

public class Number {
    private int number;

    public Number(int number) {
        checkPositiveNumber(number);
        this.number = number;
    }

    private void checkPositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수 입력 에러");
        }
    }

    public int getNumber() {
        return number;
    }

    public Number sum(Number number) {
        return new Number(this.number + number.number);
    }

}
