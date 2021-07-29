package calculator;

public class Number {
    private final int number;

    private Number(final int number) {
        this.number = number;
    }

    public static Number of(final String numberText) {
        try {
            Integer.parseInt(numberText);
        } catch (final NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 텍스트를 더할 수는 없습니다.");
        }
        final int number = Integer.parseInt(numberText);
        if (number < 0) {
            throw new RuntimeException("음수를 전달할 경우 RuntimeException 예외가 발생합니다.");
        }
        return new Number(number);
    }

    public int toInteger() {
        return number;
    }
}
