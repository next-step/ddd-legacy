package calculator.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizerFactory {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final String text;

    public TokenizerFactory(String text) {
        this.text = text;
    }

    public Tokenizer createTokenizer() {
        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) {
            return new CustomTokenizer(matcher);
        }
        return new DefaultTokenizer(text);
    }
}
