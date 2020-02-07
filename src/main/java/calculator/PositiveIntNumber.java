package calculator;

public class PositiveIntNumber {
    private int number;

    public PositiveIntNumber(String stringNumber) {
        this.number = stringToInt(stringNumber);
    }

    private int stringToInt(String input) {
        try {
            return checkMinus(Integer.parseInt(input));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int checkMinus(int number) {
        if (number < 0) throw new RuntimeException();

        return number;
    }

    public int getNumber() {
        return number;
    }
}
