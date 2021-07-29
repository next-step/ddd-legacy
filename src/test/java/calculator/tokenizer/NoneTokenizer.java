package calculator.tokenizer;

public class NoneTokenizer implements Tokenizer {

    private final String text;

    public NoneTokenizer(String text) {
        this.text = text;
    }

    @Override
    public String[] split() {
        return new String[] { this.text };
    }
}
