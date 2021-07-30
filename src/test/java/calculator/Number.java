package calculator;

public class Number {
    private final Integer number;

    public Number(String text) {
        Integer number = parseInt(text);
        checkNegative(number);
        this.number = number;
    }

    private void checkNegative(Integer number) {
        if (number < 0) {
            throw new RuntimeException("음수는 계산할 수 없습니다.");
        }
    }

    private static Integer parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new RuntimeException(text + "는 숫자가 아닙니다.");
        }
    }

    public Integer getNumber() {
        return number;
    }
}
