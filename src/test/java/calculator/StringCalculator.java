package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private final Delimiter delimiter;

    public StringCalculator() {
        this(new DefaultDelimiter());
    }

    public StringCalculator(final Delimiter delimiter) {
        this.delimiter = delimiter;
    }

    public int add(final String text) {
        if (Strings.isBlank(text)) {
            return 0;
        }
        final PositiveInt positiveInt = Arrays.stream(getStrings(text))
                .map(PositiveInt::new)
                .reduce(PositiveInt.zero(), PositiveInt::add);
        return positiveInt.value();
    }

    private String[] getStrings(final String text) {
        final Pattern pattern = Pattern.compile("//(.)\\\\n(.*)");
        final Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2).split(matcher.group(1));
        }
        return delimiter.split(text);
    }
}
