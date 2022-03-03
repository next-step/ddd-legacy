package calculator;

public class StringCalculator {

    private static final int DEFAULT_VALUE_ZERO = 0;

    public int add(String text) {
        InputText inputText = new InputText(text);

        if (inputText.isNullOrEmpty()) {
            return DEFAULT_VALUE_ZERO;
        }

        return new Numbers(inputText.separate()).sum();
    }
}
