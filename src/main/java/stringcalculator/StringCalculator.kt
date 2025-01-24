package stringcalculator

import java.util.regex.Pattern

class StringCalculator(
    private val text: String?
) {
    fun sum(): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        return when {
            hasNegativeNumber() -> throw IllegalArgumentException("not allowed negative number")
            hasCustomDelimiter() -> sumWithCustomDelimiter()
            else -> sumWithDefaultDelimiters()
        }
    }

    private fun hasNegativeNumber(): Boolean {
        return NEGATIVE_NUMBER_REGEX_PATTERN.matcher(text).find()
    }

    private fun hasCustomDelimiter(): Boolean {
        return CUSTOM_DELIMITER_PATTERN.matcher(text).find()
    }

    private fun sumWithCustomDelimiter(): Int {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(text)
        matcher.find()

        val customDelimiter: String = matcher.group(CUSTOM_DELIMITER_GROUP_INDEX)
        return matcher.group(NUMBERS_WITH_DEFAULT_DELIMITERS_REMAIN_GROUP_INDEX)
            .split(customDelimiter, *DEFAULT_DELIMITERS).sumOf { it.toInt() }
    }

    private fun sumWithDefaultDelimiters(): Int {
        val splits = text!!.split(*DEFAULT_DELIMITERS)
        if (splits.size <= 1) {
            return text.toInt()
        }

        return splits.sumOf { it.toInt() }
    }

    companion object {
        const val CUSTOM_DELIMITER_GROUP_INDEX = 1
        const val NUMBERS_WITH_DEFAULT_DELIMITERS_REMAIN_GROUP_INDEX = 2
        val DEFAULT_DELIMITERS = arrayOf(",", ":")
        val NEGATIVE_NUMBER_REGEX_PATTERN: Pattern = Pattern.compile("-\\d+")
        val CUSTOM_DELIMITER_PATTERN: Pattern = Pattern.compile("//(.)\n(.*)")
    }
}
