package stringcalculator

class StringCalculator {

    private val expressionMatcher = ExpressionMatcher()
    private val numberTokenizer = NumberTokenizer()

    fun calculate(calculatorExpression: String?): Int {
        if (calculatorExpression.isNullOrEmpty()) return DEFAULT_VALUE

        val expression = expressionMatcher.transform(calculatorExpression)
        val numbers = numberTokenizer.tokenize(expression)
        validateNegativeValue(numbers)

        return numbers.sum()
    }

    private fun validateNegativeValue(tokens: List<Int>) {
        if (tokens.any { it < 0 }) {
            throw RuntimeException("음수의 덧셈은 제공하지 않는 기능입니다")
        }
    }

    companion object {
        private const val DEFAULT_VALUE = 0
    }
}
