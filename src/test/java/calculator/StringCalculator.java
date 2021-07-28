package calculator;

public class StringCalculator {
    private static final int DEFAULT_RESULT = 0;

    public int add(String text) {
        if (isEmptyText(text)) {
            return DEFAULT_RESULT;
        }

        TextSeparator separator = new TextSeparator(text);
        return separator.getNumbers().stream()
                .mapToInt(Integer::valueOf)
                .sum();
    }

    private boolean isEmptyText(String text) {
        return text == null || text.isEmpty();
    }
}
