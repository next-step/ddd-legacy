package calculator;

record CustomStringSplitter(String value, String splitter) implements StringSplitter {

    private static final String CUSTOM_INPUT_SEPARATOR_FORMAT = "[,|:|%s]";

    @Override
    public String[] split() {
        return this.value == null
            ? new String[]{}
            : value.split(String.format(CUSTOM_INPUT_SEPARATOR_FORMAT, splitter));
    }
}
