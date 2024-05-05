package stringcalculator;

public class DefaultStrategy implements CalculatorStrategy{
    private static final String DEFAULT_SEPARATOR = "[,:]";

    @Override
    public boolean isCustom(final String input) {
        return false;
    }

    @Override
    public String getSeparator(final String input) {
        return DEFAULT_SEPARATOR;
    }
}
