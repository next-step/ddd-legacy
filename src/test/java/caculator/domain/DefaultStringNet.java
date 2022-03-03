package caculator.domain;

public class DefaultStringNet implements StringNet {

    private static final String DEFAULT_DELIMITER = "[,:]";

    @Override
    public String[] strain(String includedDelimiter) {
        return includedDelimiter.split(DEFAULT_DELIMITER);
    }
}
