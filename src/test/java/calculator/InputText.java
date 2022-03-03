package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputText {

    private static final Pattern PATTERN_CUSTOM_SEPARATOR = Pattern.compile("//(.)\n(.*)");
    private static final String SEPARATOR_SIGN = ",|:";

    private String text;

    public InputText(String text) {
        this.text = text;
    }

    public boolean isNullOrEmpty() {
        return text == null || text.isEmpty();
    }

    public String[] separate() {
        Matcher m = PATTERN_CUSTOM_SEPARATOR.matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split(SEPARATOR_SIGN);
    }
}
