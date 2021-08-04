package calculator;

import calculator.calculate.AddCalculateStrategy;
import calculator.number.PositiveNumber;
import calculator.number.PositiveNumbers;
import calculator.tokenizer.Tokenizer;
import calculator.tokenizer.TokenizerFactory;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public int add(final String text) {
        if (!StringUtils.hasText(text)) {
            return PositiveNumber.ZERO.getNumber();
        }
        final TokenizerFactory tokenizerFactory = new TokenizerFactory(text);
        final Tokenizer tokenizer = tokenizerFactory.createTokenizer();

        return PositiveNumbers.of(tokenizer.split())
                .calculate(new AddCalculateStrategy())
                .getNumber();
    }
}
