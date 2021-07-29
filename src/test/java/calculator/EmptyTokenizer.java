package calculator;

public class EmptyTokenizer implements Tokenizer {

    @Override
    public String[] split() {
        return new String[0];
    }
}
