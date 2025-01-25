package calculator;

import calculator.strategy.TextDelimiters;
import calculator.vo.PositiveInt;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Optional;

public class StringCalculator {
    private final TextDelimiters textDelimiters;

    public StringCalculator() {
        this(new TextDelimiters());
    }

    public StringCalculator(final TextDelimiters textDelimiters) {
        this.textDelimiters = textDelimiters;
    }

    public PositiveInt calculate(final String text) {
        return Optional.ofNullable(text)
                .filter(Strings::isNotBlank)
                .map(this::split)
                .orElseGet(PositiveInt::zero);
    }

    private PositiveInt split(final String text) {
        return Optional.ofNullable(text)
                .map(textDelimiters::split)
                .map(this::sum)
                .orElseGet(PositiveInt::zero);
    }

    private PositiveInt sum(final List<String> positive) {
        return positive.stream()
                .map(PositiveInt::new)
                .reduce(PositiveInt::add)
                .orElseGet(PositiveInt::zero);
    }
}
