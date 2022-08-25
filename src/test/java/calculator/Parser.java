package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String[] tokens;

    public Parser execute(String text) {
        parsingTextByCustomDelimiter(text);
        if (this.isEmptyToken()) parsingText(text);

        return this;
    }

    public String[] getTokens() {
        return this.tokens;
    }

    private boolean isEmptyToken() {
        return (this.tokens == null || this.tokens.length <= 0);
    }

    private void parsingText(String text) {
        this.tokens = text.split(",|:");
    }

    private boolean isNumeric(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.matches("\\d+");
    }

    private void parsingTextByCustomDelimiter(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            this.tokens = Arrays.stream(m.group(2).split("\\" + customDelimiter))
                    .map(x -> {
                        if (!isNumeric(x)) throw new RuntimeException("숫자가 아닙니다.");
                        return x;
                    })
                    .toArray(String[]::new);
        }
    }
}