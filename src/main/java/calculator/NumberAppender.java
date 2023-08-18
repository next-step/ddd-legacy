package calculator;

import java.util.ArrayList;

public class NumberAppender {
    private final Delimiter delimiter;
    private final StringBuilder numberBuilder = new StringBuilder();
    private final NumberStrings numberStrings = new NumberStrings(new ArrayList<>());

    public NumberAppender(final TargetString targetString) {
        delimiter = new Delimiter(targetString);
    }

    public void appendToNumberStrings(final char ch) {
        boolean isNotDigit = !Character.isDigit(ch);
        boolean isDelimiter = delimiter.contains(ch);
        if (isNotDigit && !isDelimiter) {
            throw new RuntimeException(ch + "는 허용된 구분자가 아니거나 숫자가 아닙니다.");
        }

        if (isDelimiter) {
            numberStrings.addIfNotEmpty(numberBuilder);
            return;
        }
        numberBuilder.append(ch);
    }

    public NumberStrings getNumberStrings() {
        numberStrings.addIfNotEmpty(numberBuilder);
        return numberStrings;
    }
}
