package kitchenpos.stringcalculator;

public class PositiveNumber {
    private final int value;

    public PositiveNumber(String numberString) {
        try {
            int number = Integer.parseInt(numberString);
            if (number < 0) {
                throw new IllegalArgumentException("음수는 포함될 수 없습니다: " + number);
            }
            this.value = number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자 이외의 값을 입력할 수 없습니다: " + numberString);
        }
    }

    public int getValue() {
        return value;
    }
}