package calculator

private val CUSTOMIZED_DELIMITER_PATTERN = "//(.)\n(.*)".toRegex()

class CustomizedDelimiterCalculator(
    private val calculatorNumberRangeValidator: CalculatorNumberRangeValidator
) : StringCalculateStrategy {
    override fun support(text: String?): Boolean = hasCustomizedDelimiter(text)

    override fun calculate(text: String): Int {
        val customizedDelimiter = getCustomizedDelimiter(text)

        if (customizedDelimiter == null) {
            throw IllegalArgumentException("can't find delimiter")
        }

        val tokens = CUSTOMIZED_DELIMITER_PATTERN.matchEntire(text)
            ?.let { it.groups[2] }
            ?.let { it.value.split(customizedDelimiter) }
            ?: emptyList()

        calculatorNumberRangeValidator.validateTokens(tokens)

        return tokens.mapNotNull { it.toIntOrNull() }.sum()
    }

    private fun hasCustomizedDelimiter(text: String?): Boolean =
        text?.let { CUSTOMIZED_DELIMITER_PATTERN.matches(it) } ?: false

    private fun getCustomizedDelimiter(text: String?): String? =
        text?.let { CUSTOMIZED_DELIMITER_PATTERN.matchEntire(it) }
            ?.let { it.groups[1] }
            ?.let { it.value }
}
