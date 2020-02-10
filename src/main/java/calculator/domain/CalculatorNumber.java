package calculator.domain;

public class CalculatorNumber {
    private String value;

    public CalculatorNumber(String value) {
        this.value = value;
    }

    public int parse() {
        if(isNegativeNumber(value)) {
            throw new RuntimeException();
        }

        return Integer.parseInt(value);
    }

    private boolean isNegativeNumber(String number) {
        return Integer.parseInt(number) < 0;
    }
}
