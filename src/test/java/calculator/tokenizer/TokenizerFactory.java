package calculator.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizerFactory {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final String text;
    private final CustomPattern customPattern;

    public TokenizerFactory(final String text) {
        this.text = text;
        this.customPattern = new CustomPattern(text);
    }

    public Tokenizer createTokenizer() {
        if (customPattern.find()) {
            return new CustomTokenizer(customPattern);
        }
        return new DefaultTokenizer(text);
    }
}
