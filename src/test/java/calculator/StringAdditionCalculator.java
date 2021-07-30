package calculator;

public class StringAdditionCalculator {
    public Integer add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return new Numbers(text).sum();
    }
}
