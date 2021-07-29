package calculator;

public class Text {

    private static final String COMMA_DELIMITER = ",";

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

    public String[] spitComma() {
        return text.split(COMMA_DELIMITER);
    }

    public Numbers getNumbers() {
        if (isNullOrEmpty()) {
            return Numbers.ZERO;
        }
        if (isContainComma()) {
            String[] tokens = spitComma();
            return new Numbers(tokens);
        }
        return new Numbers(text);
    }
}
