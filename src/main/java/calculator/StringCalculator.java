package calculator;

public class StringCalculator {

    private final CustomDelimiterExtractor customDelimiterExtractor;
    private final TextSplitter textSplitter;


    public StringCalculator(CustomDelimiterExtractor customDelimiterExtractor, TextSplitter textSplitter) {
        this.customDelimiterExtractor = customDelimiterExtractor;
        this.textSplitter = textSplitter;
    }

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        InputText inputText = customDelimiterExtractor.extractDelimiter(text);

        StringNumbers numbers = textSplitter.splitText(inputText);

        return numbers.sum();
    }

}
