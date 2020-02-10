package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculableText {

    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[,:]");
    private static final Pattern CUSTOMIZED_TEXT_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final String text;

    public StringCalculableText(String text) {
        this.text = text;
    }

    public boolean isNullOrEmpty() {
        return Objects.isNull(text) || text.isEmpty();
    }

    public int getTotal() {
        String[] tokens = splitToTokens();
        PositiveNumbers positiveNumbers = new PositiveNumbers(tokensToPositiveNumbers(tokens));
        return positiveNumbers.getTotal();
    }

    private String[] splitToTokens() {
        if (isCustomized()) {
            return getCustomizedPatternTokens();
        }
        return getDefaultPatternTokens();
    }

    private String[] getDefaultPatternTokens() {
        return DEFAULT_DELIMITER_PATTERN.split(text);
    }

    private String[] getCustomizedPatternTokens() {
        Matcher matcher = CUSTOMIZED_TEXT_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String text = matcher.group(2);
            return text.split(customDelimiter);
        }
        throw new IllegalStateException();
    }

    private boolean isCustomized() {
        return CUSTOMIZED_TEXT_PATTERN.matcher(text).matches();
    }

    private List<PositiveNumber> tokensToPositiveNumbers(String[] tokens) {
        return Arrays.stream(tokens)
            .map(PositiveNumber::of)
            .collect(Collectors.toList());
    }
}
