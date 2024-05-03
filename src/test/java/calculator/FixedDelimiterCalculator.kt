package calculator

private val FIXED_DELIMITERS = listOf(":", ",")

class FixedDelimiterCalculator(
    private val calculatorNumberRangeValidator: CalculatorNumberRangeValidator
) : StringCalculateStrategy {
    override fun support(text: String?): Boolean =
        text != null && hasFixedDelimiter(text)


    override fun calculate(text: String): Int {
        val tokens = text.split(*FIXED_DELIMITERS.toTypedArray())

        calculatorNumberRangeValidator.validateTokens(tokens)

        return tokens.mapNotNull { it.toIntOrNull() }.sum()
    }

    private fun hasFixedDelimiter(text: String): Boolean =
        FIXED_DELIMITERS.any { text.contains(it) }
}
