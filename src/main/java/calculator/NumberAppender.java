package calculator;

import java.util.ArrayList;

public class NumberAppender {
    private final Delimiter delimiter = new Delimiter();
    private static final StringBuilder numberBuilder = new StringBuilder();
    private final NumberStrings numberStrings = new NumberStrings(new ArrayList<>());

    public NumberAppender(final TargetString targetString) {
        numberBuilder.setLength(0);
        delimiter.addNewDelimiterIfExist(targetString);
    }

    public void appendToNumberStrings(final char ch) {
        if (delimiter.contains(ch)) {
            numberStrings.addIfNotEmpty(numberBuilder);
            return;
        }
        if (!Character.isDigit(ch)) throw new RuntimeException(ch + "는 허용된 구분자가 아니거나 숫자가 아닙니다.");
        numberBuilder.append(ch);
    }

    public NumberStrings getNumberStrings() {
        numberStrings.addIfNotEmpty(numberBuilder);
        return numberStrings;
    }
}
