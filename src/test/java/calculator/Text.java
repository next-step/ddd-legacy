package calculator;

public class Text {

    private String text;

    public Text(String text) {
        this.text = text;
    }

    public boolean isNullOrEmpty() {
        return text == null
                || text.isEmpty();
    }

    public boolean isContainComma() {
        return text.contains(",");
    }

    public String[] spitComma() {
        return text.split(",");
    }
}
