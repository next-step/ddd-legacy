package calculator

private val FIXED_DELIMITERS = listOf(":", ",")

class FixedDelimiterCalculator : StringCalculateStrategy {
    override fun support(text: String): Boolean = hasFixedDelimiter(text)

    override fun calculate(text: String): NumberToken =
        text.split(*FIXED_DELIMITERS.toTypedArray())
            .let { it.getNumberTokens() }
            .let { it.calculate() }

    private fun hasFixedDelimiter(text: String): Boolean =
        FIXED_DELIMITERS.any { text.contains(it) }
}
