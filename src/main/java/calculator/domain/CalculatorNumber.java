package calculator.domain;

public class CalculatorNumber {
    private final int number;

    public CalculatorNumber() {
        this.number = 0;
    }

    public CalculatorNumber(String value) {
        this.number = Integer.parseInt(value);

        if (isNegativeNumber(this.number)) {
            throw new RuntimeException();
        }
    }

    private CalculatorNumber(int value) {
        this.number = value;
    }

    private boolean isNegativeNumber(int number) {
        return number < 0;
    }

    public int getNumber() {
        return this.number;
    }

    public CalculatorNumber sum(CalculatorNumber number) {
        return new CalculatorNumber(this.number + number.number);
    }
}
