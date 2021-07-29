package calculator;

public class StringCalculator {

    public int add(final String text) {
        Text input = new Text(text);
        Numbers numbers = input.getNumbers();
        return numbers.add();
    }
}
