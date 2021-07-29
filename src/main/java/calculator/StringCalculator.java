package calculator;

public class StringCalculator {
    private static final int ZERO = 0;

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return ZERO;
        }
        return Numbers.of(text)
                .toIntList().stream()
                .mapToInt(Integer::valueOf)
                .sum();
    }
}
