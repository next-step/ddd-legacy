package calculator;

public class Text {

    private String text;

    public Text(String text) {
        this.text = text;
    }

    public Numbers getNumbers() {
        if (isNullOrEmpty()) {
            return Numbers.ZERO;
        }
        TokenizerFactory tokenizerFactory = new TokenizerFactory(text);
        Tokenizer tokenizer = tokenizerFactory.createTokenizer();
        return new Numbers(tokenizer.split());
    }

    private boolean isNullOrEmpty() {
        return text == null
                || text.isEmpty();
    }
}
