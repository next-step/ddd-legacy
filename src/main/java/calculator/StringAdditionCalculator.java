package calculator;

import java.util.List;

/**
 * 입력된 문자열을 처리하고 결과를 반환하는 역할
 */
public class StringAdditionCalculator {

    private final String text;

    public StringAdditionCalculator(final String text) {
        this.text = text;
    }

    public int add() {
        if (text == null || text.isEmpty()) {
            return 0;

        }

        List<String> numbers = StringSplitter.split(text);
        PositiveNumbers positiveNumbers = new PositiveNumbers(numbers);
        return positiveNumbers.sum();
    }
}

