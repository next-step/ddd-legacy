package calculator;

public class PositiveNumber {

    private final int value;


    public PositiveNumber(int value) {
        validatePositive(value);
        this.value = value;
    }

    public PositiveNumber(String stringValue) {
        int parsedInt = Integer.parseInt(stringValue);
        validatePositive(parsedInt);
        this.value = parsedInt;
    }

    private void validatePositive(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return value == ((PositiveNumber) obj).getValue();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
