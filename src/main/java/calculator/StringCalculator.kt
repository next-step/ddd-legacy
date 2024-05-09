package calculator

import java.util.regex.Pattern

class StringCalculator {
    companion object {
        private val defaultDelimiters: Regex = "[,:]".toRegex()
        private val customDelimiterPattern: Pattern = Pattern.compile("//(.)\n(.*)")
        private const val CUSTOM_DELIMITER = 1
        private const val CALCULATE_TARGET = 2
    }

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        if (text.contains("-")) {
            throw RuntimeException("음수는 사용할 수 없습니다.")
        }

        if (text.length == 1) {
            return text.toIntOrNull() ?: throw RuntimeException("숫자만 입력 가능합니다.")
        }

        val matcher = customDelimiterPattern.matcher(text)
        if (matcher.find()) {
            val customDelimiter = matcher.group(CUSTOM_DELIMITER)
            return matcher.group(CALCULATE_TARGET)
                .split(customDelimiter)
                .sumOf { it.toInt() }
        }

        return text.split(defaultDelimiters)
            .sumOf { it.toInt() }
    }
}
