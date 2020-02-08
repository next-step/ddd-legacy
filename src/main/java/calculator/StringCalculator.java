package calculator;

public class StringCalculator {

    public static int calculate(String text) {
        StringCalculableText calculableText = StringCalculableText.of(text);
        if (calculableText.isNullOrEmpty()) {
            return 0;
        }
        return calculableText.getTotal();
    }
}
