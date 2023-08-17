package stringcalculator;

public class PositiveNumber {

    private final int value;

    private final String NEGATIVE_NUMBER_ERROR_MESSAGE = "음수는 허용되지 않습니다.";

    public PositiveNumber(String numberStr) {
        int parsedValue = Integer.parseInt(numberStr.trim());
        if (parsedValue < 0) {
            throw new RuntimeException(NEGATIVE_NUMBER_ERROR_MESSAGE);
        }
        this.value = parsedValue;
    }

    public int getValue() {
        return value;
    }

}
