package calculate;

public class PositiveNumber {
    private final int number;

    public PositiveNumber(String number) {
        this.number = parseNumber(number);
    }

    private int parseNumber(String numberString) {
        int number = parseInt(numberString);
        if (number < 0) {
            throw new RuntimeException("숫자는 음수일 수 없습니다.");
        }
        return number;
    }

    private int parseInt(String numberString) {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효한 문자열이 아닙니다.");
        }
    }

    public int getNumber() {
        return number;
    }
}
