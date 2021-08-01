package calculator;

public class StringAdditionCalculator {
    private static final Integer EMPTY_RESULT = 0;

    public Integer add(String text) {
        if (text == null || text.isBlank()) {
            return EMPTY_RESULT;
        }
        return new Numbers(text).sum();
    }
}
