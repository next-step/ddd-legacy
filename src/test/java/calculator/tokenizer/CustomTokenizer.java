package calculator.tokenizer;

public class CustomTokenizer implements Tokenizer {

    private static final int FIRST = 1;
    private static final int SECOND = 2;

    private CustomPattern pattern;

    public CustomTokenizer(CustomPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String[] split() {
        String customDelimiter = pattern.group(FIRST);
        return pattern.group(SECOND).split(customDelimiter);
    }
}
