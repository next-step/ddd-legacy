package calculator

import java.lang.IllegalArgumentException
import java.util.regex.Pattern

private val CUSTOMIZED_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")

class CustomizedDelimiterCalculator(
    private val calculatorNumberRangeValidator: CalculatorNumberRangeValidator
) : StringCalculateStrategy {
    override fun support(text: String?): Boolean = hasCustomizedDelimiter(text)

    override fun calculate(text: String?): Int {
        val customizedDelimiter = getCustomizedDelimiter(text)

        if (text == null || customizedDelimiter == null) {
            throw IllegalArgumentException("can't find delimiter")
        }

        val tokens = CUSTOMIZED_DELIMITER_PATTERN.matcher(text)
            .takeIf { it.find() }
            ?.group(2)
            ?.split(customizedDelimiter)
            ?: emptyList()

        calculatorNumberRangeValidator.validateTokens(tokens)

        return tokens.mapNotNull { it.toIntOrNull() }.sum()
    }

    private fun hasCustomizedDelimiter(text: String?): Boolean =
        text?.let { CUSTOMIZED_DELIMITER_PATTERN.matcher(it) }
            ?.let { it.find() }
            ?: false

    private fun getCustomizedDelimiter(text: String?): String? =
        text?.let { CUSTOMIZED_DELIMITER_PATTERN.matcher(it) }
            ?.takeIf { it.find() }
            ?.let { it.group(1) }
}