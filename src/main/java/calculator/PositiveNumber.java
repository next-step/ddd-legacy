package calculator;

public class PositiveNumber {
    private static final int ZERO_NUMBER = 0;
    private final int positiveNumber;

    public PositiveNumber(int number) {
        this.positiveNumber = number;
    }

    public PositiveNumber(String number) {
        validatePositiveNumber(number);
        this.positiveNumber = Integer.parseInt(number);
    }

    public PositiveNumber add(String number, int addValue) {
        validatePositiveNumber(number);
        return new PositiveNumber(Integer.parseInt(number) + addValue);
    }

    public final int positiveNumber() {
        return positiveNumber;
    }

    private void validatePositiveNumber(String splitNumbers) {
        validateNumberIsDigit(splitNumbers);
        minusNumberCheck(splitNumbers);
    }

    private void validateNumberIsDigit(String splitNumbers) {
        if (!splitNumbers.chars().allMatch(Character::isDigit)) {
            throw new NotPositiveNumberException();
        }
    }

    private void minusNumberCheck(String splitNumbers) {
        if (Integer.parseInt(splitNumbers) < ZERO_NUMBER) {
            throw new NotPositiveNumberException();
        }
    }
}
