package calculator

import java.util.regex.Pattern

class TextParser {
    companion object {
        private val DEFAULT_DELIMITERS = arrayOf(",", ":")
        private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")
    }

    fun parse(text: String): Numbers {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(text)

        return if (matcher.find()) {
            val customDelimiter = matcher.group(1)
            Numbers.create(arrayOf(customDelimiter), matcher.group(2))
        } else {
            Numbers.create(DEFAULT_DELIMITERS, text)
        }
    }
}