package calculator;

public class StringCalculator {

    public int add(final String text) {
        PositiveNumbers positiveNumbers = new PositiveNumbers(text);
        return positiveNumbers.sum();
    }
}
