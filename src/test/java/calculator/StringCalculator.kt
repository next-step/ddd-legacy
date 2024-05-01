package calculator

import java.util.regex.Pattern

class StringCalculator {
    companion object {
        private val DEFAULT_DELIMITERS = arrayOf(",", ":")
        private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")
    }

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) return 0

        val tokens = parseTokens(text).apply { validate(this) }

        return tokens.sumOf { it.toInt() }
    }

    private fun parseTokens(text: String): List<String> {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(text)

        return if (matcher.find()) {
            val customDelimiter = matcher.group(1)
            matcher.group(2).split(*arrayOf(customDelimiter))
        } else {
            text.split(*DEFAULT_DELIMITERS)
        }
    }

    private fun validate(strings: List<String>) {
        require(strings.all { it.toIntOrNull() != null }) {
            throw IllegalArgumentException("숫자가 아닌 값이 포함되어 있습니다.")
        }

        require(strings.all { it.toInt() >= 0 }) {
            throw IllegalArgumentException("음수는 입력할 수 없습니다.")
        }
    }
}
