package stringcalculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringValidation {

    private final String text;

    public String getText() {
        return text;
    }

    public StringValidation(String text) {
        this.text = validateNum(text);
    }

    public boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public boolean checkNegative(String text) {
        return text.matches(ValidationRegex.NEGATIVE_NUM_REGEX.getRegex());
    }

    public String validateNum(String text) {
        String resultNum = text;
        if (isNullOrEmpty(text)) {
            resultNum = "0";
        } else if (checkNegative(text)) {
            throw new RuntimeException();
        }

        return resultNum;

    }
}
