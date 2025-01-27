package stringcalculator

import java.util.regex.Pattern

class ExpressionAnalyzer {

    fun analyze(expressionStr: String): Expression {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(expressionStr)

        return if (matcher.find()) {
            Expression(
                input = matcher.group(2),
                delimiters = DEFAULT_DELIMITER + matcher.group(1),
            )
        } else {
            Expression(
                input = expressionStr,
                delimiters = DEFAULT_DELIMITER,
            )
        }
    }

    companion object {
        private val DEFAULT_DELIMITER = listOf(",", ":")
        private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")

        data class Expression(
            val delimiters: List<String>,
            val input: String,
        )
    }
}