package calculator;

import calculator.type.TokenType;
import calculator.vo.*;

import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    public Answer add(String text) {
        UserInput userInput = getUserInput(text);

        var expression = userInput.toExpression();

        if (validateExpression(expression) == false)
            return Answer.empty();

        List<Token> tokens = parseToken(expression);

        if (isCalculable(tokens) == false)
            throw new RuntimeException("계산할 수 없는 식입니다.");

        return Answer.of(calculate(tokens));
    }

    private UserInput getUserInput(String text) {
        if (text == null)
            return UserInput.of(null, null);

        // 사용자가 커스텀한 설정값이 있다면 이를 추출한다.
        var mather = CustomRegex.CUSTOM_USER_SETTING.matcher(text);
        if (mather.find())
            return UserInput.of(mather.group(1), mather.group(2));

        return UserInput.of(null, text);
    }


    private Boolean validateExpression(String expression) {
        if (expression == null || expression.isBlank())
            return false;

        return Token.isAllTokens(expression) != false;
    }

    private List<Token> parseToken(String expression) {
        return Token
                .findAllTokens(expression).stream()
                .map(token -> CustomRegex.isDigits(token) ? ValueToken.of(token) : OperatorToken.of(token))
                .collect(Collectors.toList());
    }

    private Integer calculate(List<Token> tokens) {
        return tokens.stream()
                .filter(x -> x.getType() == TokenType.VALUE)
                .mapToInt(x -> ((ValueToken) x).value)
                .sum();
    }

    private boolean isCalculable(List<Token> tokens) {
        // 빈 문자열 검증
        if (tokens.isEmpty())
            return false;

        // 짝수번째 토큰은 숫자여야 한다.
        var evenIndexTokens = tokens.stream()
                .filter(x -> tokens.indexOf(x) % 2 == 0)
                .collect(Collectors.toList());

        if (evenIndexTokens.stream().anyMatch(x -> x.getType() != TokenType.VALUE))
            return false;

        // 홀수번쨰 토큰은 연산자(+)여야 한다.
        var oddIndexTokens = tokens.stream()
                .filter(x -> tokens.indexOf(x) % 2 != 0)
                .collect(Collectors.toList());

        if (oddIndexTokens.stream().anyMatch(x -> x.getType() != TokenType.OPERATOR))
            return false;

        // 첫번째 값으로 음수가 와선 안된다.
        var first = tokens.get(0);
        return !(first instanceof ValueToken) || ((ValueToken) first).value >= 0;
    }
}
