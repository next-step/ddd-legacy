package calculator

import java.util.regex.Pattern

class StringCalculator {
    companion object {
        private val customDelimiterPattern: Pattern = Pattern.compile("//(.)\n(.*)")
    }

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        if (text.contains("-")) {
            throw RuntimeException()
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

        return -1
    }
}
