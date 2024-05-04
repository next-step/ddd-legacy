package calculator

private val CUSTOMIZED_DELIMITER_PATTERN = "//(.)\n(.*)".toRegex()

class CustomizedDelimiterCalculator : StringCalculateStrategy {
    override fun support(text: String): Boolean = hasCustomizedDelimiter(text)

    override fun calculate(text: String): Int {
        val customizedDelimiter = getCustomizedDelimiter(text)

        if (customizedDelimiter == null) {
            throw IllegalArgumentException("can't find delimiter")
        }

        return CUSTOMIZED_DELIMITER_PATTERN.matchEntire(text)
            ?.let { it.groups[2] }
            ?.let { it.value.split(customizedDelimiter) }
            ?.let { it.getNumberTokens() }
            ?.let { it.calculate() }
            ?: throw RuntimeException("can't find valid tokens")
    }

    private fun hasCustomizedDelimiter(text: String?): Boolean =
        text?.let { CUSTOMIZED_DELIMITER_PATTERN.matches(it) } ?: false

    private fun getCustomizedDelimiter(text: String): String? =
        text.let { CUSTOMIZED_DELIMITER_PATTERN.matchEntire(it) }
            ?.let { it.groups[1] }
            ?.let { it.value }
}
