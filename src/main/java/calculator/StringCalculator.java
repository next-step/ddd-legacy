package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

public class StringCalculator {
    static final Splitter DEFAULT_SPLITTER = new Splitter(",|:");
    private final Splitter splitter;

    public StringCalculator() {
        this(DEFAULT_SPLITTER);
    }

    public StringCalculator(Splitter splitter) {
        if (splitter == null) { throw new IllegalArgumentException(); }
        this.splitter = splitter;
    }

    public int add(final String text) {
        return splitter.split(text)
                       .sum();
    }

    static class Splitter {
        private final Pattern pattern;

        public Splitter(String regex) {
            if (Strings.isBlank(regex)) { throw new IllegalArgumentException(); }
            this.pattern = Pattern.compile(regex);
        }

        public PositiveNumbers split(String text) {
            return Strings.isBlank(text) ? PositiveNumbers.EMPTY
                                         : new PositiveNumbers(Arrays.stream(pattern.split(text))
                                                                     .map(PositiveNumber::from)
                                                                     .collect(Collectors.toList()));
        }
    }
}
