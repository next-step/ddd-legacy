package calculator;

import java.util.Arrays;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        StringParser stringParser = StringParser.of(text);
        String numberText = stringParser.getNumberText();
        String delimiter = stringParser.getDelimiter();
        try {
            return Arrays.stream(numberText.split(delimiter))
                    .mapToInt(Integer::parseInt)
                    .map(this::validateNumber)
                    .sum();
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 텍스트를 더할 수는 없습니다.");
        }
    }

    private int validateNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수를 전달할 경우 RuntimeException 예외가 발생합니다.");
        }
        return number;
    }
}
