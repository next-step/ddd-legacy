package calculator;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text == "") {
            return 0;
        }
        return Integer.parseInt(text);
    }
}
