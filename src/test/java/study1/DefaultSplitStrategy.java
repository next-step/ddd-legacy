package study1;

public class DefaultSplitStrategy implements SplitStrategy {

    private static final String DELIM_REGEX = ",|:";

    @Override
    public String[] split(final String text) {
        return text.split(DELIM_REGEX);
    }
}
