package calculator;


public class StringCalculator {

    private final TextInputHandler textInputHandler = new TextInputHandler();

    public int add(String text) {
        if (textInputHandler.isBlank(text)) {
            return 0;
        }
        String[] tokens = textInputHandler.tokenize(text);
        Numbers numbers = new Numbers(tokens);
        return numbers.sum();
    }
}
