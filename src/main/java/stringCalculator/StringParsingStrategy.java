package stringCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParsingStrategy implements ParsingStrategy{
    private static final Pattern CUSTOM_PATTERN;

    static {
        CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    }

    @Override
    public InputExpression parse(final String text) {
        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) { //Custom 구분자
            Delimiters delimiters = Delimiters.customize(matcher.group(1));
            List<String> tokens = parseTokens(delimiters, matcher.group(2));
            return new InputExpression(delimiters, Numbers.from(tokens));
        }
        Delimiters delimiters = Delimiters.ofDefaults();
        List<String> tokens = parseTokens(delimiters, text);
        return new InputExpression(Delimiters.ofDefaults(), Numbers.from(tokens));
    }

    private static List<String> parseTokens(final Delimiters delimiters, final String text) {
        String delimiterExpression = String.join("|", delimiters.list());
        return Arrays.stream(text.split(delimiterExpression)).toList();
    }

}
