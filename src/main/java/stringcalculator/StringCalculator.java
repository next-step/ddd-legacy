package stringcalculator;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int number = Integer.parseInt(text);
        return number;
    }
}
