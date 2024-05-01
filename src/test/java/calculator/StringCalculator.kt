package calculator

import java.util.regex.Pattern

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        val matcher = Pattern.compile("//(.)\n(.*)").matcher(text)

        return when {
            matcher.find() -> {
                val customDelimiter = matcher.group(1)
                matcher.group(2)
                    .split(regex = customDelimiter.toRegex())
                    .sumOf { it.toInt() }
            }

            else -> text.split(regex = "[,|:]".toRegex())
                .sumOf { it.toInt() }
        }
    }
}
