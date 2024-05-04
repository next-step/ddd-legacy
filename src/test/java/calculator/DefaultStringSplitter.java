package calculator;

record DefaultStringSplitter(String value) implements StringSplitter {

    private static final String REGEX = "[,|:]";

    @Override
    public String[] split() {
        return this.value == null ? new String[]{} : this.value.split(REGEX);
    }
}
