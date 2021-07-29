package calculator;

public class Text {

    private String text;
    private Tokenizer tokenizer;

    public Text(String text, Tokenizer tokenizer) {
        this.text = text;
        this.tokenizer = tokenizer;
    }

    public Numbers getNumbers() {
        return new Numbers(tokenizer.split());
    }
}
