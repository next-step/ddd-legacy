package calculator;

import java.util.regex.Matcher;

public class CustomTokenizer implements Tokenizer {

    private Matcher matcher;

    public CustomTokenizer(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public String[] split() {
        String customDelimiter = matcher.group(1);
        return matcher.group(2).split(customDelimiter);
    }
}
