package calculator;

import org.apache.logging.log4j.util.Strings;

public class StringCalculator {
    private final TextDelimiters textDelimiters;

    public StringCalculator() {
        this(new TextDelimiters());
    }

    public StringCalculator(final TextDelimiters textDelimiters) {
        this.textDelimiters = textDelimiters;
    }

    public int add(final String text) {
        if (Strings.isBlank(text)) {
            return 0;
        }
        final PositiveInt positiveInt = textDelimiters.split(text)
                .stream()
                .map(PositiveInt::new)
                .reduce(PositiveInt.zero(), PositiveInt::add);
        return positiveInt.value();
    }
}
