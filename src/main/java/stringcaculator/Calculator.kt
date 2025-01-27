package stringcaculator

import java.util.regex.Pattern

class Calculator() {
    val ZERO = 0
    val DELIMITERS = arrayOf(",", ":")
    val CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)"


    fun add(text: String?): Int {
        var result = ZERO

        if (text.isNullOrEmpty()) {
            return ZERO
        }
        val matcher = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(text)
        val nums = when {
            matcher.find() -> {
                val customDelimiter = matcher.group(1)
                split(matcher.group(2), customDelimiter)
            }

            else -> {
                split(text, *DELIMITERS)
            }
        }

        nums.map {
            result += it.num.toInt()
        }
        return result
    }

    private fun split(text: String, vararg delimiters: String): List<Number> {
        return text.split(*delimiters).map { Number(it) }
    }
}
