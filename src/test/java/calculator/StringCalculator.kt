package calculator

import java.util.regex.Pattern

class StringCalculator {
    companion object {
        private val DEFAULT_DELIMITERS = arrayOf(",", ":")
        private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")
    }

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) return 0

        return parseTokens(text).sum()
    }

    private fun parseTokens(text: String): Tokens {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(text)

        return if (matcher.find()) {
            val customDelimiter = matcher.group(1)
            Tokens.create(arrayOf(customDelimiter), matcher.group(2))
        } else {
            Tokens.create(DEFAULT_DELIMITERS, text)
        }
    }
}
