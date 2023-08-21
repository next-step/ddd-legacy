package calculator;

public class DelimiterSplit {
    private static final String DELIMITER = ",|:";

    public String[] split(final String text) {
        return text.split(DELIMITER);
    }
}
