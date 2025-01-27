package mission.step1;

public class StringCalculator {

    public static final String DELIMITER = "[,:]";

    public String[] splitWithDelimiter(String expression) {
        return expression.split(DELIMITER);
    }

    public int toInt(String expression) {
        try {
            int number = Integer.parseInt(expression);
            validatePositiveNumber(number);
            return number;
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 형식이 아닌 값이 포함되어 있습니다: " + expression);
        }
    }

    private void validatePositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수는 허용되지 않습니다: " + number);
        }
    }
}
