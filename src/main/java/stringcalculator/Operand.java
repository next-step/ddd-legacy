package stringcalculator;

public class Operand {
    private final int value;

    public int getValue() {
        return value;
    }

    public Operand(String strValue) {
        this.value = toInt(strValue);
        if (this.value < 0) {
            throw new RuntimeException("음수를 입력할 수 없습니다");
        }
    }

    private int toInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자만 입력하세요");
        }
    }
}
