package calculator.model;

public class CalcNumber {
    public int value;
    private static final int DEFAULT_CALC_VALUE = 0;

    public CalcNumber(){
        this.value = DEFAULT_CALC_VALUE;
    }

    public CalcNumber(int value) {
        this.value = value;
        this.validate(value);
    }

    public CalcNumber(String str) {
        this(Integer.parseInt(str));
    }

    public int getValue() {
        return this.value;
    }

    private void validate(int value) {
        if (value < 0) {
            throw new RuntimeException("Number must be positive");
        }
    }

    public CalcNumber sum(CalcNumber num) {
        return new CalcNumber(this.getValue() + num.getValue());
    }
}
