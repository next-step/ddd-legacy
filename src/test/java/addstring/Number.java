package addstring;

public class Number {

    private final int number;

    public Number(String stringNumber) {
        validateNumber(stringNumber);
        this.number = Integer.parseInt(stringNumber);
    }

    private void validateNumber(String stringNumber) {
        boolean hasNegativeNumber = Integer.parseInt(stringNumber) < 0;

        if (hasNegativeNumber) {
            throw new IllegalArgumentException("음수를 입력할 수 없습니다.");
        }
    }

    public int getIntValue() {
        return this.number;
    }
}
