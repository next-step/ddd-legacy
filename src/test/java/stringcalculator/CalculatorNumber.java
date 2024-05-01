package stringcalculator;

public class CalculatorNumber {
    private int number;

    public CalculatorNumber(String number) {
        this.number = validateNumber(number);
    }

    public int validateNumber(String number) {
        try {
            int generatedNumber = Integer.parseInt(number);
            if (generatedNumber < 0) {
                throw new IllegalArgumentException("음수값은 처리할 수 없습니다.");
            }
            return generatedNumber;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("올바른 숫자 입력 값이 아닙니다.");
        }
    }

    public int getNumber() {
        return number;
    }
}
