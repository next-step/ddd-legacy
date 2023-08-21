package calculator;

import calculator.type.TokenType;
import calculator.vo.ExpressionCustomizer;
import calculator.vo.OperatorToken;
import calculator.vo.Token;
import calculator.vo.ValueToken;

import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    private static final Integer ZERO = 0;

    public Integer add(String text) {
        ExpressionCustomizer expressionCustomizer = getExpressionCustomizer(text);

        var expression = expressionCustomizer.toExpression();

        if (!isValidExpression(expression))
            return ZERO;

        List<Token> tokens = parseToken(expression);

        if (!isCalculable(tokens))
            throw new IllegalArgumentException("계산할 수 없는 식입니다.");

        return calculate(tokens);
    }

    private ExpressionCustomizer getExpressionCustomizer(String text) {
        if (text == null)
            return ExpressionCustomizer.of(null, null);

        // 사용자가 커스텀한 설정값이 있다면 이를 추출한다.
        var mather = CustomRegex.EXPRESSION_CUSTOMIZER.matcher(text);
        if (mather.find())
            return ExpressionCustomizer.of(mather.group(1), mather.group(2));

        return ExpressionCustomizer.of(null, text);
    }


    private Boolean isValidExpression(String expression) {
        if (expression == null || expression.isBlank())
            return false;

        if (!Token.isAllTokens(expression))
            return false;

        return true;
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
        if (tokens.isEmpty())
            return false;

        var evenIndexTokens = tokens.stream()
                .filter(x -> tokens.indexOf(x) % 2 == 0)
                .collect(Collectors.toList());

        if (isAllValue(evenIndexTokens) == false)
            return false;

        var oddIndexTokens = tokens.stream()
                .filter(x -> tokens.indexOf(x) % 2 != 0)
                .collect(Collectors.toList());

        if (isAllOperator(oddIndexTokens) == false)
            return false;

        if (isNegativeValue(tokens.get(0)))
            return false;

        return true;
    }

    private Boolean isAllValue(List<Token> tokens) {
        return tokens.stream().allMatch(x -> x.getType() == TokenType.VALUE);
    }

    private Boolean isAllOperator(List<Token> tokens) {
        return tokens.stream().allMatch(x -> x.getType() == TokenType.OPERATOR);
    }

    private Boolean isNegativeValue(Token token) {
        return token instanceof ValueToken && ((ValueToken) token).value < 0;
    }

}
