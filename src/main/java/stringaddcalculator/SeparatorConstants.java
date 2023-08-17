package stringaddcalculator;

public enum SeparatorConstants {
    DEFAULT_DELIMITER(",|:"),
    CUSTOM_DELIMITER_PATTERN("//(.)\n(.*)");

    private final String value;

    SeparatorConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
