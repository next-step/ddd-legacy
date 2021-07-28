package calculator;

public class Number {
    private final int value;

    public Number(String value) {
        this.value = parseInt(value);
        validateAmniotic();
    }

    private void validateAmniotic() {
        if (this.value < 0) {
            throw new RuntimeException("양수만 계산이 가능합니다.");
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new RuntimeException("숫자만 계산이 가능합니다.");
        }
    }

    public int intValue() {
        return value;
    }
}
