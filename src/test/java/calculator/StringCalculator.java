package calculator;

public class StringCalculator {
    public int add(String text) {
        if (isBlank(text)) {
            return 0;
        }
        return Numbers.convertToNumbers(text).sum();
    }

    private boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}
