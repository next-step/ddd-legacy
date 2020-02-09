package calculator;

public class StringCalculator {

    private static final int EMPTY_TEXT_RESULT = 0;

    public static int calculate(String text) {
        StringCalculableText calculableText = new StringCalculableText(text);
        if (calculableText.isNullOrEmpty()) {
            return EMPTY_TEXT_RESULT;
        }
        return calculableText.getTotal();
    }
}
