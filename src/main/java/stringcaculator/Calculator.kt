package stringcaculator

import java.util.regex.Pattern

class Calculator() {
    private val ZERO = 0
    private val DEFAULT_DELIMITERS = arrayOf(",", ":")
    private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")
    private val DELIMITER_GROUP = 1
    private val BODY_GROUP = 2


    fun add(text: String?): Int {
        if (text.isNullOrEmpty()) {
            return ZERO
        }

        val (delimiter, body) = parseInput(text)
        return Numbers.generateNumbers(body, delimiter).sum()
    }

    private fun parseInput(text: String): Pair<Array<String>, String> {
        val matcher = CUSTOM_DELIMITER_PATTERN.matcher(text)
        return matcher.takeIf { it.find() }?.let {
            arrayOf(it.group(DELIMITER_GROUP)) to it.group(BODY_GROUP)
        } ?: (DEFAULT_DELIMITERS to text)
    }
}
