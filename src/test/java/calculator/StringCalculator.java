package calculator;

import calculator.calculate.AddCalculateStrategy;
import calculator.calculate.CalculateStrategy;
import calculator.tokenizer.Tokenizer;
import calculator.tokenizer.TokenizerFactory;

public class StringCalculator {

    public int add(final String text) {
        TokenizerFactory tokenizerFactory = new TokenizerFactory(text);
        Tokenizer tokenizer = tokenizerFactory.createTokenizer();

        Text input = new Text(text, tokenizer);

        Numbers numbers = input.getNumbers();
        CalculateStrategy addStrategy = new AddCalculateStrategy();

        return numbers.calculate(addStrategy);
    }
}
