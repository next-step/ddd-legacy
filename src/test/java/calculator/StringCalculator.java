package calculator;

import calculator.calculate.AddCalculateStrategy;
import calculator.calculate.CalculateStrategy;
import calculator.number.Numbers;
import calculator.number.PositiveNumbers;
import calculator.tokenizer.Tokenizer;
import calculator.tokenizer.TokenizerFactory;

public class StringCalculator {

    public int add(final String text) {
        TokenizerFactory tokenizerFactory = new TokenizerFactory(text);
        Tokenizer tokenizer = tokenizerFactory.createTokenizer();

        Numbers numbers = new PositiveNumbers(tokenizer.split());
        CalculateStrategy addStrategy = new AddCalculateStrategy();

        return numbers.calculate(addStrategy).getNumber();
    }
}
