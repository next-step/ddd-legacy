package stringcalculator

import java.util.regex.Pattern

class StringCalculator() {

    fun add(input: String?): Int {
        if (input.isNullOrEmpty()) return 0

        val singleNumber = convertSingleNumber(input)
        singleNumber.map { return@add it }

        val (delimiters, restInput) = divideDelimiterAndInput(input)
        val tokens = splitInput(restInput, delimiters)
        val numberTokens = tokens.map { it.toInt() }

        return numberTokens.sum()
    }

    private fun convertSingleNumber(input: String): Result<Int> {
        return kotlin.runCatching { input.toInt() }
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

    companion object {
        private val DEFAULT_DELIMITER = listOf(",", ":")

        private data class DelimiterInput(
            val delimiters: List<String>,
            val input: String,
        )
    }
}
