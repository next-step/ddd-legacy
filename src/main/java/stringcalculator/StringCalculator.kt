package stringcalculator

class StringCalculator(
    private val parser: Parser,
    private val expressionEvaluator: ExpressionEvaluator
) {
    fun calculate(input: String?): Int {
        if (input.isNullOrEmpty()) {
            return 0
        }

        val result = parser.parse(input)
        val delimiters = predefinedDelimiters + result.customDelimiters

        return expressionEvaluator.evaluate(
            expression = result.expression,
            delimiters = delimiters
        )
    }

    companion object {
        private val predefinedDelimiters = setOf(
            Delimiter(","),
            Delimiter(":")
        )
    }
}
