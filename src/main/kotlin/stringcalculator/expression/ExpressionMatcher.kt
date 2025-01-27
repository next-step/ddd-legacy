package stringcalculator.expression

import java.util.regex.Pattern

object ExpressionMatcher {

    private val DEFAULT_DELIMITER = listOf(",", ":")
    private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")

    private const val GROUP_CUSTOM_DELIMITER_INDEX = 1
    private const val GROUP_INPUT_INDEX = 2

    fun transform(expression: String): Expression {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(expression)

        return if (matcher.find()) {
            Expression(
                input = matcher.group(GROUP_INPUT_INDEX),
                delimiters = DEFAULT_DELIMITER + matcher.group(GROUP_CUSTOM_DELIMITER_INDEX),
            )
        } else {
            Expression(
                input = expression,
                delimiters = DEFAULT_DELIMITER,
            )
        }
    }
}
