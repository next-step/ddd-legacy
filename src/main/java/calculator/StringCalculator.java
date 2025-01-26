package calculator;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            int number = Integer.parseInt(text);
            if (number < 0) {
                throw new RuntimeException("양의 정수를 입력하세요.");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력하세요.");
        }

        return Integer.parseInt(text);
    }


}
