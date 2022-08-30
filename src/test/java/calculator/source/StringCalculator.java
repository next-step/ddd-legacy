package calculator.source;

import calculator.source.splitter.StringSplitters;

public class StringCalculator {
    private final StringSplitters splitters;

    public StringCalculator(final StringSplitters splitters) {
        this.splitters = splitters;
    }

    public Number plus(final String input) {
        if (input == null || input.isBlank()) {
            return new Number(0);
        }
        return Numbers.from(splitters.split(input))
                .plusAll();
    }

}
