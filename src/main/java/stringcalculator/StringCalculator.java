package stringcalculator;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        Delimiter delimiter = new Delimiter(text);
        String[] tokens = delimiter.splitText();

        Numbers numbers = new Numbers(tokens);
        return numbers.sum();
    }
}
