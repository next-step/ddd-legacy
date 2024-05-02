package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculatorTokenParser {
    private final String[] tokens;

    public StringCalculatorTokenParser(String text) {

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

        this.tokens = m.find() ?
                m.group(2).split(m.group(1)) : text.split(",|:");
    }

    public int[] getIntegerTokens() {
        return Arrays.stream(tokens)
                .mapToInt(this::parseToken)
                .toArray();
    }

    private int parseToken(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing token: " + token, e);
        }
    }

}
