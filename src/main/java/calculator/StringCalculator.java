package calculator;


public class StringCalculator {
    private static final int DEFAULT_OUTPUT = 0;
    private final Parser parser;

    StringCalculator(Parser parser) {
        this.parser = parser;
    }

    public int add(final String text) {
        if (isEmpty(text)) {return DEFAULT_OUTPUT;}
        String[] tokens = parser.findTokens(text);

        return new NumberTokens(tokens).generateSum();
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}


