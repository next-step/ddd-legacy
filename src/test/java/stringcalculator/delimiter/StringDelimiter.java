package stringcalculator.delimiter;

import java.util.List;

public interface StringDelimiter {
    List<String> getStringTokens(String tokenText, String delimiterPattern);
}
