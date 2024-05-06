package stringcalculator

class ExpressionEvaluator {
    fun evaluate(
        expression: Expression,
        delimiters: Set<Delimiter>
    ): Int {
        return expression.value
            .split(delimiters = delimiters.map { it.value }.toTypedArray())
            .map { it.toIntOrNull() ?: throw RuntimeException() }
            .sumOf(::sumOrThrowWhenNegative)
    }

    private fun sumOrThrowWhenNegative(number: Int): Int {
        if (number < 0) throw RuntimeException()

        return number
    }
}
