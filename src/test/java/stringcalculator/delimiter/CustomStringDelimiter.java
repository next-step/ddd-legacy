package stringcalculator.delimiter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CustomStringDelimiter implements StringDelimiter {
    @Override
    public List<String> getStringTokens(String tokenText, String delimiterPattern) {
        return Arrays.asList((tokenText.split(Pattern.quote(delimiterPattern))));
    }
}