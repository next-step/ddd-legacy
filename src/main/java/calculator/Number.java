package calculator;

public class Number {
    private final int number;

    public Number(String text) {
        try {
            int resultNumber = Integer.parseInt(text);
            checkingNegative(resultNumber);
            this.number = resultNumber;
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 값이 존재 합니다.");
        }
    }

    public Number(int number) {
        checkingNegative(number);
        this.number = number;
    }

    private void checkingNegative(int number) {
        if (Integer.signum(number) == -1) {
            throw new RuntimeException("숫자가 음수입니다.");
        }
    }

    public Number plus(Number number) {
        return new Number(this.number + number.number);
    }

    public int getNumber() {
        return number;
    }
}
