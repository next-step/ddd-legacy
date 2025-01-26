package stringcalculator

import java.util.regex.Pattern

class StringCalculator() {

    fun add(input: String?): Int {
        if (input.isNullOrEmpty()) return 0

        val (delimiters, restInput) = divideDelimiterAndInput(input)
        val tokens = splitInput(restInput, delimiters)
        val numberTokens = tokens.map { it.toInt() }
        validateNegativeValue(numberTokens)

        return numberTokens.sum()
    }

    private fun divideDelimiterAndInput(input: String): DelimiterInput {
        val matcher = Pattern.compile("//(.)\n(.*)").matcher(input)

        return if (matcher.find()) {
            DelimiterInput(
                input = matcher.group(2),
                delimiters = DEFAULT_DELIMITER + matcher.group(1),
            )
        } else {
            DelimiterInput(
                input = input,
                delimiters = DEFAULT_DELIMITER,
            )
        }
    }

    private fun splitInput(input: String, delimiters: List<String>): List<String> {
        return input.split(delimiters.joinToString("|").toRegex())
    }

    private fun validateNegativeValue(tokens: List<Int>) {
        if (tokens.any { it < 0 }) {
            throw RuntimeException("음수의 덧셈은 제공하지 않는 기능입니다")
        }
    }

    companion object {
        private val DEFAULT_DELIMITER = listOf(",", ":")

        private data class DelimiterInput(
            val delimiters: List<String>,
            val input: String,
        )
    }
}
