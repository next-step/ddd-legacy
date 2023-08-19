package stringcalculator;

public enum CustomDelimiterGroup {
    CUSTOM_DELIMITER(1),
    NUMBERS(2);

    private final int group;

    CustomDelimiterGroup(int group) {
        this.group = group;
    }

    public int group() {
        return group;
    }
}
