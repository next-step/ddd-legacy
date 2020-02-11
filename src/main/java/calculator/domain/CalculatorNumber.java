package calculator.domain;

public class CalculatorNumber {
    private int number;

    public CalculatorNumber(String value) {
        this.number = Integer.parseInt(value);

        if (isNegativeNumber(this.number)) {
            throw new RuntimeException();
        }
    }

    private boolean isNegativeNumber(int number) {
        return number < 0;
    }

    public int getNumber() {
        return this.number;
    }
}
