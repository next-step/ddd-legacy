package calculator;

public class Text {

    private static final String COMMA_DELIMITER = ",";
    private static final String COLON_DELIMITER = ":";
    private static final String COMMA_OR_COLON_DELIMITER = ",|:";

    private String text;

    public Text(String text) {
        this.text = text;
    }

    public boolean isNullOrEmpty() {
        return text == null
                || text.isEmpty();
    }

    public boolean isContainComma() {
        return text.contains(COMMA_DELIMITER);
    }

    public boolean isContainColon() {
        return text.contains(COLON_DELIMITER);
    }

    public String[] spitCommaOrColon() {
        return text.split(COMMA_OR_COLON_DELIMITER);
    }

    public Numbers getNumbers() {
        if (isNullOrEmpty()) {
            return Numbers.ZERO;
        }
        if (isContainComma() || isContainColon()) {
            String[] tokens = spitCommaOrColon();
            return new Numbers(tokens);
        }
        return new Numbers(text);
    }
}
