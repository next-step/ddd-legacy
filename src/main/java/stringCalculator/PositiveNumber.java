package stringCalculator;

public class PositiveNumber {

    private int number;

    public PositiveNumber(String text) {
        validateParseInt(text);
        validatePositive(number);
    }

    private void validateParseInt(String text) {
        try {
            this.number = Integer.parseInt(text);
        } catch(NumberFormatException e) {
            throw new RuntimeException("숫자 이외의 값을 사용할 수 없습니다.");
        }
    }

    private void validatePositive(int number) {
        if (number < 0) {
            resetNumber();
            throw new RuntimeException("음수는 사용할 수 없습니다.");
        }
    }

    private void resetNumber() {
        this.number = 0;
    }

    public int getPositiveNumber() {
        return this.number;
    }

}
