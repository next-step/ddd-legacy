package calculator.verifier;

import java.util.List;

@Deprecated
public class PositiveNumberVerifier implements NumberVerifier {

    private static final int MIN_VALUE = 0;

    @Override
    public void verify(List<String> expressions) {
        for (String expression : expressions) {
            this.verify(expression);
        }
    }

    private void verify(final String expression) {
        if (expression.chars()
                      .noneMatch(character -> Character.isDigit(character))) {
            throw new IllegalArgumentException("숫자가 아닌 문자는 입력 불가능 합니다.");
        }

        if (Integer.parseInt(expression) < MIN_VALUE) {
            throw new IllegalArgumentException("0보다 작은 숫자는 입력 불가능합니다.");
        }
    }
}
