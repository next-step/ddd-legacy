package stringcalculator;

public class StringCalculator {

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }
        ZeroOrPositiveNumbers numbers = convertToNumbers(text);
        return numbers.sum();
    }

    private boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }

    private ZeroOrPositiveNumbers convertToNumbers(String text) {
        String[] textArray = TextSplitter.split(text);
        return new ZeroOrPositiveNumbers(textArray);
    }
}
