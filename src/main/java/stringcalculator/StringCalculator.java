package stringcalculator;

public class StringCalculator {
    private final Delimiters delimiters;

    public StringCalculator() {
        this.delimiters = new Delimiters();
    }

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        if (text.startsWith("//")) {
            text = processCustomDelimiter(text);
        }

        return sumTokens(delimiters.split(text));
    }

    private String processCustomDelimiter(String text) {
        String[] parts = text.split("\\n", 2);
        if (parts.length < 2) {
            throw new RuntimeException("Invalid input format");
        }
        String customDelimiter = parts[0].substring(2);
        delimiters.addCustomDelimiter(customDelimiter);
        return parts[1];
    }

    private int sumTokens(String[] tokens) {
        int sum = 0;
        for (String token : tokens) {
            if (!token.isEmpty()) {
                sum += NumberParser.parse(token);
            }
        }
        return sum;
    }
}