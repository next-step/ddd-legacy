package calculator.tokenizer;

public class DefaultTokenizer implements Tokenizer {

    private static final String COMMA_OR_COLON_DELIMITER = ",|:";

    private final String text;

    public DefaultTokenizer(String text) {
        this.text = text;
    }

    @Override
    public String[] split() {
        return text.split(COMMA_OR_COLON_DELIMITER);
    }
}
