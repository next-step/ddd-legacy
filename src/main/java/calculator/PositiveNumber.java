package calculator;

public class PositiveNumber {
    private static final int ZERO_NUMBER = 0;
    private final int positiveNumber;

    public PositiveNumber(String number) {
        validatePositiveNumber(number);
        this.positiveNumber = Integer.parseInt(number);
    }

    public final int add(int addValue) {
        return addValue + positiveNumber;
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
