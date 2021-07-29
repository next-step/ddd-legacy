package calculator;

public class StringCalculator {

    public int add(final String text) {
        TokenizerFactory tokenizerFactory = new TokenizerFactory(text);
        Tokenizer tokenizer = tokenizerFactory.createTokenizer();
        Text input = new Text(text, tokenizer);

        Numbers numbers = input.getNumbers();
        return numbers.add();
    }
}
