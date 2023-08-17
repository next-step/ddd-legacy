package calculator.vo;


import calculator.type.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public interface Token {
    Pattern TOKEN_PATTERN = Pattern.compile("(-?[1-9][0-9]*)|([,:])");

    static List<String> findAllTokens(String value) {
        var matcher = TOKEN_PATTERN.matcher(value);

        var matches = new ArrayList<String>();

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    static Boolean isAllTokens(String value) {
        return value.replaceAll(TOKEN_PATTERN.pattern(), "").isEmpty();

    }

    TokenType getType();
}
