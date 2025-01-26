package calculator;

public class StringCalculator {

    private final DelimiterParser delimiterParser;
    private final TextSplitter textSplitter;


    public StringCalculator(DelimiterParser delimiterParser, TextSplitter textSplitter) {
        this.delimiterParser = delimiterParser;
        this.textSplitter = textSplitter;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        InputText inputText = delimiterParser.parseInputText(text);

        Numbers numbers = textSplitter.splitText(inputText);

        return numbers.sum();
    }

}
