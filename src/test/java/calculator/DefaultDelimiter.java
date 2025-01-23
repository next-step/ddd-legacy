package calculator;

public class DefaultDelimiter implements Delimiter {
    private static final String DEFAULT_DELIMITER = ",|:";
    private final String delimiter;

    public DefaultDelimiter() {
        this(DEFAULT_DELIMITER);
    }

    public DefaultDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String[] split(final String text) {
        return text.split(delimiter);
    }
}
