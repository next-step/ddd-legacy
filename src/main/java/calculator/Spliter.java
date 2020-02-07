package calculator;

public class Spliter {
    private static final String TEXT_SPERATOR_1 = ",";
    private static final String TEXT_SPERATOR_2 = ":";

    public String[] splitText(String text) {
        String[] textArray = text.split(String.format("%s|%s", TEXT_SPERATOR_1, TEXT_SPERATOR_2));
        return textArray;
    }
}
