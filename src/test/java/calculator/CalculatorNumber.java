package calculator;

public class CalculatorNumber {
    private static final int MIN_NUMBER = 0;
    private int number;

    private CalculatorNumber() {};

    public CalculatorNumber(String number) {
        this(Integer.parseInt(number));
    }

    public CalculatorNumber(int number) {
        if (number < MIN_NUMBER) {
            throw new IllegalArgumentException("계산할 숫자는 음수를 가질수 없습니다.");
        }
    }
}
