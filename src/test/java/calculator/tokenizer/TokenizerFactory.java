package calculator.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizerFactory {

    private static final String COMMA_DELIMITER = ",";
    private static final String COLON_DELIMITER = ":";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final String text;

    public TokenizerFactory(String text) {
        this.text = text;
    }

    public Tokenizer createTokenizer() {
        if (isNullOrEmpty()) {
            return new EmptyTokenizer();
        }

        if (hasCommaOrColon()) {
            return new DefaultTokenizer(text);
        }

        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) {
            return new CustomTokenizer(matcher);
        }

        return new NoneTokenizer(text);
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

    private boolean isNullOrEmpty() {
        return text == null
                || text.isEmpty();
    }
}
