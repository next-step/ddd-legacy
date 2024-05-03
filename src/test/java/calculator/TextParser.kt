package calculator

import java.util.regex.Pattern

object TextParser {
    private val DEFAULT_DELIMITERS = arrayOf(",", ":")
    private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")

    fun parse(text: String): List<String> {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(text)

        return if (matcher.find()) {
            val customDelimiter = matcher.group(1)
            matcher.group(2).split(*arrayOf(customDelimiter))
        } else {
            text.split(*DEFAULT_DELIMITERS)
        }
    }
}
