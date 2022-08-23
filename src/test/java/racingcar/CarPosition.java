package racingcar;

public class CarPosition {

    private final int value;

    public CarPosition(int value) {
        this.value = value;
        validateNegative(this.value);
    }

    private void validateNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("위치 값은 음수일 수 없습니다.");
        }
    }

    public int getValue() {
        return value;
    }
}
