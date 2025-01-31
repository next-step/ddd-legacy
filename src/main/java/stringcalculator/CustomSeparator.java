package stringcalculator;

public class CustomSeparator {

    private String parsedInput;
    private String separator;

    public CustomSeparator() {
    }

    public CustomSeparator(String parsedInput, String separator) {
        this.parsedInput = parsedInput;
        this.separator = separator;
    }

    public boolean isParsedStatus() {
        return this.parsedInput != null && this.separator != null;
    }

    public String getParsedInput() {
        return parsedInput;
    }

    public String getSeparator() {
        return separator;
    }
}
