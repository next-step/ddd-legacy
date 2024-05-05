package stringcalculator;

enum ValidationRegex {

    NEGATIVE_NUM_REGEX("^-\\d+$"),
    POSITIVE_NUM_REGEX("^\\d+$");

    private final String regex;

    ValidationRegex(String regex) {
        this.regex = regex;
    }
    public String getRegex() {
        return regex;
    }

}