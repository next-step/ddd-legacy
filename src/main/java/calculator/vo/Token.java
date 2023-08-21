package calculator.vo;


import calculator.type.TokenType;

import java.util.ArrayList;
import java.util.List;

import static calculator.CustomRegex.TOKEN_PATTERN;

public interface Token {
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
