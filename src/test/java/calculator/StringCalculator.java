package calculator;

public class StringCalculator {
    private static final int DEFAULT_RESULT = 0;

    private final Separator separator;

    public StringCalculator() {
        this(new TextSeparator());
    }

    private StringCalculator(Separator separator) {
        this.separator = separator;
    }

    public int add(String text) {
        if (isEmptyText(text)) {
            return DEFAULT_RESULT;
        }

        return separator.separate(text)
                .sum();
    }

    private boolean isEmptyText(String text) {
        return text == null || text.isEmpty();
    }
}
