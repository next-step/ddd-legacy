package calculator;

final class DefaultStringSplitter implements StringSplitter {

    private static final String REGEX = "[,|:]";

    @Override
    public String[] split(final String value) {
        return value == null || value.isEmpty() ? new String[]{} : value.split(REGEX);
    }

    @Override
    public boolean support(String value) {
        return true;
    }
}
