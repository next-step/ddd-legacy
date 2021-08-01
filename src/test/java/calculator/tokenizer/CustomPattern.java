package calculator.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPattern {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final Matcher matcher;

    public CustomPattern(String text) {
        matcher = CUSTOM_PATTERN.matcher(text);
    }

    public boolean find() {
        return matcher.find();
    }

    public String group(int number) {
        return matcher.group(number);
    }
}
