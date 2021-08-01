package calculator;

import calculator.calculate.AddCalculateStrategy;
import calculator.calculate.CalculateStrategy;
import calculator.number.Numbers;
import calculator.number.PositiveNumbers;
import calculator.tokenizer.Tokenizer;
import calculator.tokenizer.TokenizerFactory;

public class StringCalculator {

    public int add(final String text) {
        Numbers numbers = PositiveNumbers.of(text);

        return numbers
                .calculate(new AddCalculateStrategy())
                .getNumber();
    }
}
