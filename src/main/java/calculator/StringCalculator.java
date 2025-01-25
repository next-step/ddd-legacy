package calculator;

public class StringCalculator {
    private final String text;

    public StringCalculator(final String text) {
        try {
            int number = Integer.parseInt(text);
            if (number < 0) {
                throw new RuntimeException("양의 정수를 입력하세요.");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력하세요.");
        }

        this.text = text;
    }

    public void add(final String value) {
        StringCalculator stringCalculator = new StringCalculator(value);
        stringCalculator.add(value);
    }
}
