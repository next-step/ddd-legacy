package calculator;

public class Text {

    private static final String COMMA_DELIMITER = ",";
    private static final String COLON_DELIMITER = ":";
    private static final String COMMA_OR_COLON_DELIMITER = ",|:";

    private String text;

    public Text(String text) {
        this.text = text;
    }

    public Numbers getNumbers() {
        if (isNullOrEmpty()) {
            return Numbers.ZERO;
        }
        if (hasCommaOrColon()) {
            String[] tokens = spitCommaOrColon();
            return new Numbers(tokens);
        }
        return new Numbers(text);
    }

    private boolean isNullOrEmpty() {
        return text == null
                || text.isEmpty();
    }

    private boolean hasComma() {
        return text.contains(COMMA_DELIMITER);
    }

    private boolean hasColon() {
        return text.contains(COLON_DELIMITER);
    }

    private boolean hasCommaOrColon() {
        return hasComma() || hasColon();
    }

    private String[] spitCommaOrColon() {
        return text.split(COMMA_OR_COLON_DELIMITER);
    }
}
