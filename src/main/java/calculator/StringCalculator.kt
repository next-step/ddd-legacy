package calculator

import java.util.regex.Pattern

class StringCalculator {
    companion object {
        private val defaultDelimiters: Regex = "[,:]".toRegex()
        private val customDelimiterPattern: Pattern = Pattern.compile("//(.)\n(.*)")
    }

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        if (text.contains("-")) {
            throw RuntimeException("음수는 사용할 수 없습니다.")
        }

        if (text.length == 1) {
            return text.toInt()
        }

        val matcher = customDelimiterPattern.matcher(text)
        if (matcher.find()) {
            val customDelimiter = matcher.group(1)
            return matcher.group(2)
                .split(customDelimiter)
                .sumOf { it.toInt() }
        }

        return text.split(defaultDelimiters)
            .sumOf { it.toInt() }
    }
}
