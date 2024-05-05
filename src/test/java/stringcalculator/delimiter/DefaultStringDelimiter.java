package stringcalculator.delimiter;

import java.util.Arrays;
import java.util.List;

public class DefaultStringDelimiter implements StringDelimiter {
    @Override
    public List<String> getStringTokens(String tokenText, String delimiterPattern) {
        return Arrays.asList(tokenText.split(delimiterPattern));
    }
}